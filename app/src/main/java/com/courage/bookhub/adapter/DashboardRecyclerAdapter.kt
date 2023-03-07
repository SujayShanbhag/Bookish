package com.courage.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.courage.bookhub.R
import com.courage.bookhub.activity.DescriptionActivity
import com.courage.bookhub.model.Book
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context : Context,val itemList : ArrayList<Book>)
                                :RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {
    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookName : TextView=view.findViewById(R.id.txtBookName)
        val bookAuthor : TextView=view.findViewById(R.id.txtAuthor)
        val bookPrice : TextView=view.findViewById(R.id.txtPrice)
        val bookRating : TextView=view.findViewById(R.id.txtRating)
        val bookImage : ImageView=view.findViewById(R.id.imgBook)
        val item : RelativeLayout=view.findViewById(R.id.bookItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row,parent,false)

        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book=itemList[position]
        holder.bookName.text=book.bookName
        holder.bookAuthor.text=book.bookAuthor
        holder.bookPrice.text=book.bookPrice
        holder.bookRating.text=book.bookRating
        Picasso.get().load(book.bookImg).error(R.drawable.default_book).into(holder.bookImage)

        holder.item.setOnClickListener {
            val intent= Intent(context,DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            context.startActivity(intent)
        }
    }
}