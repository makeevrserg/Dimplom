import json
import sympy as sp

# Перевод костей из TF
# Родительские кости TFLite
TFBones = {
    "NOSE": {'parent': 'NECK', "start": "head", "end": "head"},
    "NECK": {"parent": "WAIST", "start": "neck", "end": "torso"},
    # Дополнительная кость - середина между LEFT_SHOULDER & RIGHT_SHOULDER

    "LEFT_SHOULDER": {"parent": "NECK", "start": "rear-upper-arm"},
    "RIGHT_SHOULDER": {"parent": "NECK", "start": "front-upper-arm"},

    "LEFT_ELBOW": {"parent": "LEFT_SHOULDER", "start": "rear-bracer", "end": "rear-upper-arm"},
    "RIGHT_ELBOW": {"parent": "RIGHT_SHOULDER", "start": "front-bracer", "end": "front-upper-arm"},

    "LEFT_WRIST": {"parent": "LEFT_ELBOW", "start": "gun", "end": "rear-bracer"},
    "RIGHT_WRIST": {"parent": "RIGHT_ELBOW", "start": "front-fist", "end": "front-bracer"},

    "WAIST": {"parent": "WAIST", "start": "torso"},  # Дополнительная кость - середина между LEFT_HIP & RIGHT_HIP

    "LEFT_HIP": {"parent": "WAIST", "start": "rear-thigh"},
    "RIGHT_HIP": {"parent": "WAIST", "start": "front-thigh"},

    "LEFT_KNEE": {"parent": "LEFT_HIP", "start": "front-shin", "end": "front-thigh"},
    "RIGHT_KNEE": {"parent": "RIGHT_HIP", "start": "rear-shin", "end": "rear-thigh"},

    "LEFT_ANKLE": {"parent": "LEFT_KNEE", "start": "front-foot", "end": "front-shin"},
    "RIGHT_ANKLE": {"parent": "RIGHT_KNEE", "start": "rear-foot", "end": "rear-shin"}
}

SPINE_START_RIGS = [
                    'rear-upper-arm', 'front-upper-arm', 'rear-bracer','front-bracer',
                    'rear-thigh', 'front-thigh']

# key = имя кости; value = массив позиций этой кости
positions = {}

# Родительские кости Spine
spineParents = {}

max_frames = 0
real_timeline_length = 0


def calc_fps(frame, len=0):
    global max_frames, real_timeline_length
    if frame != 0:
        max_frames = max(frame, max_frames)
    if len != 0:
        real_timeline_length = max(real_timeline_length, len)


class KeyPoint:
    def __init__(self, frame, x, y):
        self.frame = frame
        self.x = x
        self.y = y


# ---------------------------------------------------Функция верна
def get_max_frames(key_points):
    m = 0
    for kp in key_points:
        if kp.frame > m:
            m = kp.frame
    return m


# key = имя кости; value = массив позиций этой кости
# BONE : arrayOf(keypoint)
# ---------------------------------------------------Функция верна
def create_positions(js):
    global positions, spineParents, tfLiteParents, TFBones, SpineBones, frames
    # Загружаем json
    anims_json = json.loads(js)
    # Проходим по каждой кости

    for bodyPart in anims_json:
        keypoints = []
        # Проходим по захватившимся фреймам
        for point in anims_json[bodyPart]:
            keypoints.append(
                KeyPoint(point['frame'],
                         point['x'],
                         point['y']))
            calc_fps(point['frame'], 0)
        calc_fps(0, len(keypoints))
        positions[bodyPart] = keypoints
    # У TFLite нет WAIST и NECK. Будет удобнее если оно будет, поэтому делаем их
    positions['WAIST'] = keypoint_between(positions['LEFT_HIP'], positions['RIGHT_HIP'])
    positions['NECK'] = keypoint_between(positions['LEFT_SHOULDER'], positions['RIGHT_SHOULDER'])
    return positions


# ---------------------------------------------------Функция верна
def get_len(a: KeyPoint, b: KeyPoint):
    return sp.sqrt(
        (b.x - a.x) ** 2 + (b.y - a.y) ** 2
    )


import math


def get_angle(child, parent,multiplier = 1):
    if parent is None or child is None: return 0
    # Надо вычислять синус угла
    if child==parent:
        parent=KeyPoint(0,0,0)
    axis = KeyPoint(0, max(parent.x, child.x), min(child.y, parent.y))
    PC = get_len(parent, child)
    PA = get_len(parent, axis)
    CA = get_len(child, axis)
    if PA != 0:
        mSin = float(CA / PC)
        mASin = sp.asin(mSin)
        rad = math.degrees(mASin) / 2*multiplier
    else:
        return 0
    while rad < -90: rad += 45
    while rad > 90: rad -= 45
    return rad - 30


def get_rotation(frame, tfPoint):
    global positions

    tfParentPoint = TFBones[tfPoint]['parent']
    spineBone = TFBones[tfPoint]['start']

    tfParentPointKeyPoints = positions[tfParentPoint]
    tfPointKeyPoints = positions[tfPoint]

    tfPointKeyPoint = get_near_keypoint_by_frame(tfPointKeyPoints, frame)
    tfParentPointKeyPoint = get_near_keypoint_by_frame(tfParentPointKeyPoints, frame)
    multiplier = 1
    if spineBone in SPINE_START_RIGS:
        multiplier=5
    return get_angle(tfPointKeyPoint, tfParentPointKeyPoint,multiplier)


# Выдает новыйе keypoint'ы между двумя точками
def keypoint_between(keypoints1, keypoints2):
    keypoints = []
    # Может быть так, что у них будет разное количество фреймов, если TFLite,например, не определит точку
    for i in range(0, max(get_max_frames(keypoints1), get_max_frames(keypoints2)) + 1):
        # Получаем keypoint по фрейму, потому что в массиве они могут быть  в неправильном порядке
        ls = get_near_keypoint_by_frame(keypoints1, i)
        rs = get_near_keypoint_by_frame(keypoints2, i)
        # Может получиться так, что одного из кейпоинтов вообще не будет - в таком случае скипаем
        if ls is not None and rs is not None:
            keypoints.append(
                KeyPoint(i,
                         (ls.x + rs.x) / 2,
                         (ls.y + rs.y) / 2)
            )
    return keypoints


def get_near_keypoint_by_frame(keypoints, frame):
    max_frames = get_max_frames(keypoints)
    max_len = len(keypoints)
    near_round = (max_frames - max_len) * 2
    keypoint = get_keypoint_by_frame(keypoints, frame)
    if keypoint is not None:
        return keypoint
    while frame > 0:
        frame -= 1
        keypoint = get_keypoint_by_frame(keypoints, frame)
        if keypoint is not None:
            if abs(keypoint.frame - frame) > near_round:
                break
            return keypoint

    while frame < max_len:
        keypoint = get_keypoint_by_frame(keypoints, frame)
        if keypoint is not None:
            if abs(keypoint.frame - frame) > near_round:
                break
            return keypoint
        frame += 1
    return None


# ---------------------------------------------------Функция верна
def get_keypoint_by_frame(keypoints, frame):
    max_frames = get_max_frames(keypoints)
    max_len = len(keypoints)
    if frame > max_frames:
        return None

    for i in range(0, max_len):
        if keypoints[i].frame == frame:
            return keypoints[i]
    return None


def get_parents(spine_json):
    global positions, spineParents, tfLiteParents, TFBones, SpineBones
    parents = {}
    for bone in spine_json:
        if 'name' in bone and 'parent' in bone:
            parents[bone['name']] = bone['parent']
    return parents


def get_spine_json():
    spine_json_file = open('spine_clear.json', 'r')
    spine_json = json.loads(spine_json_file.read())
    spine_json_file.close()
    return spine_json


def f_loose(x):
    return x * x / 600000 + 3 * x / 2000 + 5 / 6


def f(x):
    return 1 / 30 * x


# Когда пользователь захватывает анимацию - сервер запускает эту функцию.
# id - id анимации, js - .json анимации
def initJson(id: str, js: str):
    global positions, spineParents, tfLiteParents, TFBones, SpineBones, frameSpeedMultiplier, max_frames, real_timeline_length

    # Создали словарь с позициями
    # key = имя кости; value = массив позиций этой кости
    positions = create_positions(js)
    print(min(max_frames, real_timeline_length) / max(max_frames, real_timeline_length))
    if min(max_frames, real_timeline_length) / max(max_frames, real_timeline_length) < 0.5:
        frames_decreaser = f_loose(max_frames + real_timeline_length) / max_frames
    else:
        frames_decreaser = f(max_frames) / max_frames
    print('Фреймов:', max_frames)
    print('Реальных кадров:', real_timeline_length)
    print('frames_decreaser:', frames_decreaser)
    print('Инициализация')
    # animations section
    # Читаем анимацию spineboy из бэкапа
    spine_json = get_spine_json()
    # Задаем родительские кости спайна из .json
    spineParents = get_parents(spine_json['bones'])
    # Берем список анимированных костей из .json
    animation_json = spine_json['animations']['run']['bones']
    print('Очистка')
    # Очищаем списко анимаций и позиций костей
    for bone in animation_json:
        animation_json[bone]['translate'] = [{}]
        animation_json[bone]['rotate'] = [{}]
    print('Конвертация')

    for bone in positions:
        if bone in {'NOSE', 'LEFT_EYE', 'RIGHT_EYE', 'LEFT_EAR', 'RIGHT_EAR'}:
            continue
        keypoints = positions[bone]
        spine_bone = TFBones[bone]['start']
        parent_tfpoint = TFBones[bone]['parent']
        print('TFPoint {}; TFparent {}; SpineBone {}'.format(bone, parent_tfpoint, spine_bone))
        rotations = []
        for keypoint in keypoints:
            if keypoint.frame == 0:
                rotations.append({'angle': get_rotation(keypoint.frame, bone)})
            rotations.append({'angle': get_rotation(keypoint.frame, bone), 'time': keypoint.frame * frames_decreaser})
            continue
        animation_json[spine_bone]['rotate'] = rotations

    print('Запись')
    spine_json['animations']['run']['bones'] = animation_json
    with open('spineboy/' + id + '.json', 'w') as outFile:
        json.dump(spine_json, outFile, sort_keys=True, indent=4)

    frame_multiplier = int(80 / real_timeline_length)
    print(frame_multiplier * real_timeline_length)
    # Тут создаётся гифка
    import threading, subprocess
    threading.Thread(
        target=subprocess.Popen,
        args=(['java13', '-jar', 'GDX.jar', str(id), str(frame_multiplier * real_timeline_length)],)
    ).start()


#
# Здесь тестовый запуск
# with open('spineboy/TF_ANIM_SMALL.json', 'r') as file:
#     initJson(str(8), file.read())
