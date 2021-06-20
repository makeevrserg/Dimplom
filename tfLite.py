from flask import Flask, request, jsonify, make_response, send_file
from flask_sqlalchemy import SQLAlchemy
import uuid
from werkzeug.security import generate_password_hash, check_password_hash
import jwt
import datetime
from functools import wraps
import os,time

app = Flask(__name__)
file_path = os.path.abspath(os.getcwd()) + "\\data.db"
app.config['SECRET_KEY'] = 'thisissecret'
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + file_path
db = SQLAlchemy(app)


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50))
    password = db.Column(db.String(80))


class Anims(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    json_input = db.Column(db.String(100))
    unix_time = db.Column(db.Integer)
    user_id = db.Column(db.Integer)


def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None
        print(request)
        print(request.headers)
        if 'x-access-token' in request.headers:
            token = request.headers['x-access-token']
        if not token:
            return jsonify({'message': 'Token is missing!'}), 401
        try:
            data = jwt.decode(token, app.config['SECRET_KEY'])
            current_user = User.query.filter_by(public_id=data['public_id']).first()
        except:
            return jsonify({'message': 'Token is invalid!'}), 401
        return f(current_user, *args, **kwargs)
    return decorated


@app.route('/users', methods=['GET'])
@token_required
def get_all_users(current_user):
    if not current_user.admin:
        return jsonify({'message': 'Cannot perform that function!'})
    users = User.query.all()
    output = []
    for user in users:
        user_data = {}
        user_data['public_id'] = user.public_id
        user_data['name'] = user.name
        user_data['password'] = user.password
        user_data['admin'] = user.admin
        output.append(user_data)

    return jsonify({'users': output})


@app.route('/register', methods=['POST'])
def create_user():
    data = request.get_json()
    hashed_password = generate_password_hash(data['password'], method='sha256')

    user = User.query.filter_by(name=data['name']).first()
    if user:
        return jsonify({'message': 'User already exists'})

    new_user = User(public_id=str(uuid.uuid4()), name=data['name'], password=hashed_password, admin=False)
    db.session.add(new_user)
    db.session.commit()

    return jsonify({'message': 'New user created!', 'user': data['name'], 'password': data['password']})


@app.route('/login')
def login():
    auth = request.authorization
    print(request)
    print(request.authorization)
    print(request.headers)
    if not auth or not auth.username or not auth.password:
        return make_response('Could not verify', 401, {'WWW-Authenticate': 'Basic realm="Login required!"'})

    user = User.query.filter_by(name=auth.username).first()

    if not user:
        return make_response('Could not verify', 401, {'WWW-Authenticate': 'Basic realm="Login required!"'})

    if check_password_hash(user.password, auth.password):
        token = jwt.encode(
            {'public_id': user.public_id, 'exp': datetime.datetime.utcnow() + datetime.timedelta(minutes=30)},
            app.config['SECRET_KEY'])

        return jsonify({'token': token.decode('UTF-8')})
    return make_response('Could not verify', 401, {'WWW-Authenticate': 'Basic realm="Login required!"'})


@app.route('/anims/get/<anims_id>', methods=['GET'])
@token_required
def get_one_anim(current_user, anim_id):
    anim = Anims.query.filter_by(id=anim_id, user_id=current_user.id).first()

    if not anim:
        return jsonify({'message': 'No anim found!'})

    anim_data = {}
    anim_data['id'] = anim.id
    anim_data['json_input'] = anim.json_input
    anim_data['json_output'] = anim.json_output
    anim_data['unix_time'] = anim.unix_time

    return jsonify(anim_data)


@app.route('/imgs/<pid>.gif')
def get_image(pid):
    print(pid)
    if pid + '.gif' not in os.listdir('imgs'):
        return '<h3>Изображение не найдено. Возможно, оно ещё грузится. Пожалуйста, подождите.</h3>'

    return send_file('imgs/' + pid + '.gif')


@app.route('/anims', methods=['GET'])
@token_required
def get_all_anim(current_user):
    anims = Anims.query.filter_by(user_id=current_user.id).all()

    output = []

    for anim in anims:
        anim_data = {}
        anim_data['id'] = anim.id
        anim_data['json_input'] = anim.json_input
        anim_data['unix_time'] = anim.unix_time
        output.append(anim_data)
    return jsonify({'anims': output})


@app.route('/anims_idtime', methods=['GET'])
@token_required
def get_all_anims_idtime(current_user):
    anims = Anims.query.filter_by(user_id=current_user.id).all()
    output = []
    for anim in anims:
        anim_data = {}
        anim_data['id'] = anim.id
        anim_data['unix_time'] = anim.unix_time
        output.append(anim_data)
    return jsonify({'anims': output})


from TF_To_Spine import initJson

import threading


@app.route('/anim/new', methods=['POST'])
@token_required
def create_anim(current_user):
    data = str(request.get_json())

    print(data)

    tTime = int(time.time())
    new_anim = Anims(json_input=data,
                     unix_time=tTime, user_id=current_user.id)

    db.session.add(new_anim)
    db.session.commit()
    anim = Anims.query.filter_by(user_id=current_user.id, unix_time=tTime, json_input=data).first()
    threading.Thread(initJson(str(anim.id), data.replace('\'','"'))).start()

    return jsonify({'message': "anim created!"})


@app.route('/anim/delete/<anim_id>', methods=['DELETE'])
@token_required
def delete_anim(current_user, anim_id):
    anim = Anims.query.filter_by(id=anim_id, user_id=current_user.id).first()

    if not anim:
        return jsonify({'message': 'No anim found!'})

    db.session.delete(anim)
    db.session.commit()

    return jsonify({'message': 'anim item deleted!'})


if __name__ == '__main__':
    app.run(debug=True, host="192.168.1.3", port=5000)
