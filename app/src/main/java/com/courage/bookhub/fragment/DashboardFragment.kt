package com.courage.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.courage.bookhub.R
import com.courage.bookhub.adapter.DashboardRecyclerAdapter
import com.courage.bookhub.model.Book
import com.courage.bookhub.util.ConnectionManager
import org.json.JSONException
import java.util.Collections

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {


    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout : RelativeLayout
    lateinit var progressBar : ProgressBar


    val bookInfoList=arrayListOf<Book>()
    lateinit var recyclerAdapter :DashboardRecyclerAdapter

    var ratingComparator= Comparator<Book>{book1,book2 ->
            if(book1.bookRating.compareTo(book2.bookRating,true)==0){
                book1.bookName.compareTo(book2.bookName,true)
            }
            else {
                book1.bookRating.compareTo(book2.bookRating,true)
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        recyclerDashboard=view.findViewById(R.id.recyclerDashboard)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        layoutManager= LinearLayoutManager(activity)

        val queue =Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v1/book/fetch_books/"

        progressLayout.visibility=View.VISIBLE
        if(ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener {
                    val success = it.getBoolean("success")
                    try {
                        progressLayout.visibility=View.GONE
                        if (success) {
                            setHasOptionsMenu(true)
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJsonObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image")
                                )
                                bookInfoList.add(bookObject)
                            }

                            recyclerAdapter =
                                DashboardRecyclerAdapter(activity as Context, bookInfoList)

                            recyclerDashboard.adapter = recyclerAdapter
                            recyclerDashboard.layoutManager = layoutManager

                            recyclerDashboard.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerDashboard.context,
                                    (layoutManager as LinearLayoutManager).orientation
                                )
                            )
                        } else {
                            if(activity !=null)
                                Toast.makeText(activity as Context, "Some Error has occured", Toast.LENGTH_LONG).show()
                        }
                    }
                    catch (e:JSONException) {
                        if(activity !=null)
                            Toast.makeText(activity as Context,"$e",Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {
                    if(activity !=null)
                        Toast.makeText(activity as Context, "$it", Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val header = HashMap<String, String>()
                    header["Content-type"] = "application/json"
                    header["token"] = "9bf534118365f1"
                    return header
                }
            }
            queue.add(jsonObjectRequest)
        }
        else{
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error!")
            dialog.setMessage("NO Internet Connection")
            dialog.setPositiveButton("Open Settings"){text, listener->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent
                )
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){text,listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item?.itemId
        if(id==R.id.action_sort){
            Collections.sort(bookInfoList,ratingComparator)
            bookInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

}