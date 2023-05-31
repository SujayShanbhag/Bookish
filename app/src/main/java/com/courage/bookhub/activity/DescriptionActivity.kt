package com.courage.bookhub.activity

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.courage.bookhub.R
import com.courage.bookhub.database.BookDatabase
import com.courage.bookhub.database.BookEntity
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {
    lateinit var bookName : TextView
    lateinit var bookAuthor : TextView
    lateinit var bookPrice : TextView
    lateinit var bookRating : TextView
    lateinit var bookImg : ImageView
    lateinit var bookDescription : TextView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var btnAddToFav : Button
    lateinit var toolbar: Toolbar

    var bookId :String?="100"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        bookName=findViewById(R.id.txtBookName)
        bookAuthor=findViewById(R.id.txtBookAuthor)
        bookPrice=findViewById(R.id.txtBookPrice)
        bookRating=findViewById(R.id.txtBookRating)
        bookImg=findViewById(R.id.imgBook)
        bookDescription=findViewById(R.id.bookDescription)
        progressLayout=findViewById(R.id.progressLayout)
        progressBar=findViewById(R.id.progressBar)
        btnAddToFav=findViewById<Button>(R.id.btnAddToFav)
        toolbar=findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        toolbar.title="Book Details"

        progressLayout.visibility= View.VISIBLE
        progressBar.visibility=View.VISIBLE
        if(intent!=null){
            bookId=intent.getStringExtra("book_id")
        }
        else{
            Toast.makeText(this,"Some unexpected error occured",Toast.LENGTH_LONG).show()
            finish()
        }
        if(bookId=="100"){
            Toast.makeText(this,"Some unexpected error occured",Toast.LENGTH_LONG).show()
            finish()
        }
        val queue=Volley.newRequestQueue(this)
        val url="https://dull-puce-vulture-suit.cyclic.app/get_book/"
        val jsonParams=JSONObject()
        jsonParams.put("book_id",bookId)

        val jsonObjectRequest= object : JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener {
            val success=it.getBoolean("success")
            try {
                if(success){
                    progressLayout.visibility=View.GONE
                    val jsonBookObject=it.getJSONObject("book_data")
                    val bookImgUrl=jsonBookObject.getString("image")

                    bookName.text=jsonBookObject.getString("name")
                    bookAuthor.text=jsonBookObject.getString("author")
                    bookPrice.text=jsonBookObject.getString("price")
                    bookRating.text=jsonBookObject.getString("rating")
                    bookDescription.text=jsonBookObject.getString("description")
                    Picasso.get().load(bookImgUrl).error(R.drawable.default_book).into(bookImg)

                    val bookEntity=BookEntity(
                        bookId?.toInt() as Int,
                        bookName.text.toString(),
                        bookAuthor.text.toString(),
                        bookRating.text.toString(),
                        bookPrice.text.toString(),
                        bookDescription.text.toString(),
                        bookImgUrl
                    )

                    val checkFav=DBAsyncTask(applicationContext,bookEntity,1).execute()
                    val isFav=checkFav.get()

                    if(isFav){
                        btnAddToFav.text="Remove from favourites"
                        val favColor=ContextCompat.getColor(applicationContext,R.color.favColor)
                        btnAddToFav.setBackgroundColor(favColor)
                    }
                    else {
                        btnAddToFav.text="Add to favourites"
                        val notFavColor=ContextCompat.getColor(applicationContext,R.color.primary)
                        btnAddToFav.setBackgroundColor(notFavColor)
                    }

                    btnAddToFav.setOnClickListener {
                        if(DBAsyncTask(applicationContext,bookEntity,1).execute().get()) {//check if book in fav
                            val async =  DBAsyncTask(applicationContext,bookEntity,3).execute()
                            val result=async.get()
                            if(result) {
                                Toast.makeText(this,"Book removed to Favourites",Toast.LENGTH_SHORT).show()
                                btnAddToFav.text="Add to favourites"
                                val notFavColor=ContextCompat.getColor(applicationContext,R.color.primary)
                                btnAddToFav.setBackgroundColor(notFavColor)
                            }
                            else {
                                Toast.makeText(this,"Some error occured",Toast.LENGTH_SHORT).show()
                            }
                        }
                        else {//not in favourites
                            val async=DBAsyncTask(applicationContext,bookEntity,2).execute()
                            val result=async.get()
                            if(result) {
                                Toast.makeText(this,"Book Added to Favourites",Toast.LENGTH_SHORT).show()
                                btnAddToFav.text="Remove from favourites"
                                val favColor=ContextCompat.getColor(applicationContext,R.color.favColor)
                                btnAddToFav.setBackgroundColor(favColor)
                            }
                            else {
                                Toast.makeText(this,"Some error occured",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }
                else {
                    Toast.makeText(this,"Some error occured",Toast.LENGTH_LONG).show()
                }
            }
            catch(e:JSONException){
                Toast.makeText(this,"$e",Toast.LENGTH_LONG).show()
            }
        },Response.ErrorListener {
            Toast.makeText(this,"$it",Toast.LENGTH_LONG).show()
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val header=HashMap<String,String>()
                header["Content-type"]="application/json"
                header["token"]="9bf534118365f1"
                return header
            }
        }
        queue.add(jsonObjectRequest)
    }

    class DBAsyncTask(val context : Context,val bookEntity: BookEntity,val mode : Int): AsyncTask<Void,Void,Boolean>() {

        val db= Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when(mode) {
                1 -> {
                    //check whether book is in favourites
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book!=null
                }

                2 -> {
                    //add book to favourites
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }

                3 -> {
                    //remove book from favourites
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }
}