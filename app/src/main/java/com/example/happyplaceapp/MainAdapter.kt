package com.example.happyplaceapp
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaceapp.databinding.ItemHappyPlaceBinding

class MainAdapter(
    var items: ArrayList<registerEntity>,
    var context:Context,
    var onItemClick: ((registerEntity) -> Unit)? = null

):
    RecyclerView.Adapter<MainAdapter.MainHolder>() {


    inner class MainHolder(var binding: ItemHappyPlaceBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: registerEntity?) {

            val llTitle = binding.tvTitle
            val llDescription = binding.tvDescription
            val llImage = binding.ivPlaceImage


            binding.root.setOnClickListener {
                onItemClick!!.invoke(item!!)
            }
            llTitle.text = item?.title.toString()
            llDescription.text = item?.description.toString()
            llImage.setImageURI(Uri.parse(item?.image))

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(ItemHappyPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        context = holder.itemView.context
        val item = items[position]
        holder.bind(item)

    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int){

        val intent = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra("data", items[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
         return items.size
    }
}
