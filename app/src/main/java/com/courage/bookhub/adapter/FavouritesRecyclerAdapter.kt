package com.courage.bookhub.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.courage.bookhub.R
import com.courage.bookhub.database.BookEntity
import com.squareup.picasso.Picasso

class FavouritesRecyclerAdapter(val context: Context,val bookList: List<BookEntity>): RecyclerView.Adapter<FavouritesRecyclerAdapter.FavouritesViewHolder>() {
    class FavouritesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtBookName : TextView=view.findViewById(R.id.bookName)
        val txtBookAuthor : TextView=view.findViewById(R.id.bookAuthor)
        val txtBookPrice: TextView=view.findViewById(R.id.bookPrice)
        val txtBookRating : TextView=view.findViewById(R.id.bookRating)
        val txtBookImage : ImageView=view.findViewById(R.id.bookImg)
        val bookItem : RelativeLayout=view.findViewById(R.id.favBookItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val view=LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_favourites_single_book,parent,false)

        return FavouritesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {

        val book=bookList[position]

        holder.txtBookName.text=book.bookName
        holder.txtBookAuthor.text=book.bookAuthor
        holder.txtBookPrice.text=book.bookPrice
        holder.txtBookRating.text=book.bookRating
        Picasso.get().load(book.bookImage).error(R.drawable.default_book).into(holder.txtBookImage)
    }
}