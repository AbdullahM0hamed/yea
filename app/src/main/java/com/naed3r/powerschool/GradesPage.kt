package com.naed3r.powerschool

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.items.AbstractItem
import khttp.get
import khttp.post
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.doAsync
import org.jsoup.Jsoup
import kotlin.concurrent.thread

var combined = mutableListOf<String>()
var clsNames = mutableListOf<String>()
var avgScores = mutableListOf<String>()

class GradesPage : AppCompatActivity() {
    lateinit var grades: MutableList<String>

    private fun scrape() {
        val data = doAsync {
            try {
                val soup = get("https://homeaccess.beth.k12.pa.us/HomeAccess/Account/LogOn/index.html")

                val jsoup = Jsoup.parse(soup.text)
                val veriToken = jsoup.getElementsByTag("input").attr("name", "__RequestVerificationToken").attr("value")

                val Username = intent.getStringExtra("Username")
                val Password = intent.getStringExtra("Password")
                //val oldToken = intent.getStringExtra("Token")
                val Token = veriToken//intent.getStringExtra("Token")

                val payload = mapOf("__RequestVerificationToken" to Token, "ReturnUrl" to "/HomeAccess/Classes/Classwork", "SCKTY00328510CustomEnabled" to false, "Database" to 10, "LogOnDetails.UserName" to Username, "LogOnDetails.Password" to Password)
                val headers = mapOf("Host" to "homeaccess.beth.k12.pa.us", "Referer" to "https://homeaccess.beth.k12.pa.us/HomeAccess/Classes/Classwork")

                val posted = post("https://homeaccess.beth.k12.pa.us/HomeAccess/Account/LogOn/index.html?ReturnUrl=%2fhomeaccess", data = payload, allowRedirects = true, cookies = soup.cookies)
                val r = posted.url
                Log.d("PostRes", "$posted, $r")

                val soupi = Jsoup.parse(posted.text)
                val dataFrame = soupi.select("iframe").attr("src")
                Log.d("Tag", "https://homeaccess.beth.k12.pa.us$dataFrame")

                val Link: String = "https://homeaccess.beth.k12.pa.us$dataFrame"
                val GradesLocation = post(Link, cookies = soup.cookies, data = payload, headers = headers)//, data = payload

                Log.d("PostRes", "$GradesLocation")
                val soupy = Jsoup.parse(GradesLocation.text)

                for (cls in soupy.select("a.sg-header-heading")) {
                    clsNames.add(cls.text())
                }

                for (cls in soupy.select("span.sg-header-heading.sg-right")) {
                    if (cls.text() == "" || cls.text() == null) {
                        avgScores.add("Unknown Value")
                    } else {
                        avgScores.add(cls.text())
                    }

                }

                Log.d("Sel", "$clsNames, \n $avgScores")

                for ((i, cls) in clsNames.withIndex()) {
                    combined.add(cls)
                    combined.add(avgScores[i])
                }

                Log.d("Combined", "$combined")



            } catch (e: Exception) {
                Log.e("Exception", e.toString())
            }
        }

        setContentView(R.layout.activity_grades_page)
        val dataBox = findViewById<TextView>(R.id.textView2)
        dataBox.text = combined.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grades_page)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            scrape()
        }
        val recyclerView: RecyclerView = findViewById<RecyclerView>(R.id.recycling)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = Adapter(combined)
    }

    class Adapter(private val values: List<String>): RecyclerView.Adapter<Adapter.ViewHolder>() {
        override fun getItemCount() = values.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.text_row_item, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder?.textView?.text = values[position]
        }

        class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
            var textView: TextView? = null
            init {
                textView = itemView?.findViewById(R.id.classNameGrade)
            }
        }



    }
}



/*
class BindingIconItem : AbstractBindingItem<BindingIconItem>() {
    var name: String? = null

    override val type: Int
        get() = R.id.classNameGrade

    override fun bindView(binding: BindingIconItem, payloads: List<Any>) {
        binding.name.text = name
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): BindingIconItem {
        return BindingIconItem.inflate(inflater, parent, false)
    }
}

 */
/*
class SimpleItem : AbstractItem<SimpleItem.ViewHolder>() {
    var data: String? = null
    //var description: String? = null

    /** defines the type defining this item. must be unique. preferably an id */
    override val type: Int
        get() = R.id.classNameGrade

    /** defines the layout which will be used for this item in the list */
    override val layoutRes: Int
        get() = R.layout.text_row_item

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<SimpleItem>(view) {
        var name: TextView = view.findViewById(R.id.classNameGrade)
        //var description: TextView = view.findViewById(R.id.material_drawer_description)

        override fun bindView(item: SimpleItem, payloads: List<Any>) {
            name.text = item.data
            //description.text = item.name
        }

        override fun unbindView(item: SimpleItem) {
            name.text = null
            //description.text = null
        }
    }
}

 */

/*
class GradesAdapter (private val mContacts: List<String>) : RecyclerView.Adapter<GradesAdapter.ViewHolder>()
{
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val nameTextView = itemView.findViewById<TextView>(R.id.classNameGrade)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradesAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val gradeView = inflater.inflate(R.layout.text_row_item, parent, false)
        // Return a new holder instance
        return ViewHolder(gradeView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: GradesAdapter.ViewHolder, position: Int) {
        // Get the data model based on position
        val grade = mContacts.get(position)
        // Set item views based on your views and data model
        val textView = viewHolder.nameTextView
        textView.text = grade
        //val button = viewHolder.messageButton
        //button.text = if (contact.isOnline) "Message" else "Offline"
        //button.isEnabled = contact.isOnline
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mContacts.size
    }
}
*/

/*
class UserListActivity : AppCompatActivity() {
    lateinit var grades: MutableList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
        // Lookup the recyclerview in activity layout
        val rvGrades = findViewById<View>(R.id.recycling) as RecyclerView
        // Initialize contacts
        //contacts = Contact.createContactsList(20)
        // Create adapter passing in the sample user data
        val adapter = GradesAdapter(combined)
        // Attach the adapter to the recyclerview to populate items
        rvGrades.adapter = adapter
        // Set layout manager to position the items
        rvGrades.layoutManager = LinearLayoutManager(this)
        // That's all!
    }
}
 */

/*
class CustomAdapter(private val dataSet: Array<String>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    //val myRecycler =

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {

            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.textView)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
 */