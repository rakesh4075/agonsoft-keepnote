package com.keepnote

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.keepnote.R
import com.keepnote.databinding.CardImageBinding

class AddImageRecyclerview(var imageList: ArrayList<Bitmap?>):RecyclerView.Adapter<AddImageRecyclerview.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardImageBinding: CardImageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.card_image,parent,false)
        return ViewHolder(cardImageBinding)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.image)
            .load(imageList[position])
            .into(holder.image)
    }

    class ViewHolder(cardImageBinding: CardImageBinding):RecyclerView.ViewHolder(cardImageBinding.root) {
        val image = cardImageBinding.editimage
    }
}