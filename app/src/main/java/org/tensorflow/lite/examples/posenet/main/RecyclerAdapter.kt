package org.tensorflow.lite.examples.posenet.main

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.tensorflow.lite.examples.posenet.R
import java.util.*

class RecyclerAdapter(private val mContext:Context,private val dataSet: HashMap<Int,Long>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
var TAG = "RecyclerAdapter"
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewId: TextView
        val textViewTime: TextView
        val layout: LinearLayout
        init {
            // Define click listener for the ViewHolder's View.
            textViewId = view.findViewById(R.id.textViewId)
            textViewTime = view.findViewById(R.id.textViewTime)
            layout = view.findViewById(R.id.layout)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row, viewGroup, false)

        return ViewHolder(view)
    }

    fun getTime(old:Long,new:Long):String{
        val time = new-old;
        if (time<60)
            return "${time} секунд назад";
        else
            return "${time/60} часов назад";
    }
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textViewId.text = "id = ${dataSet.keys.elementAt(position).toString()}"
        val mills = dataSet.values.elementAt(position)/1000
        viewHolder.textViewTime.text = ""


        viewHolder.layout.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "onBindViewHolder: $position")
            Log.d(TAG, "onBindViewHolder: Sending ${dataSet.keys.elementAt(position)}")
            val intent = Intent(mContext,GifViewActivity::class.java)
            intent.putExtra("ID", dataSet.keys.elementAt(position))
            mContext.startActivity(intent)
        })
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
