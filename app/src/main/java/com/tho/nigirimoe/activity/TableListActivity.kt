package com.tho.nigirimoe.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.View
import com.tho.nigirimoe.R
import com.tho.nigirimoe.fragment.TableListFragment
import com.tho.nigirimoe.model.Course
import com.tho.nigirimoe.model.MenuList
import com.tho.nigirimoe.model.Table
import kotlinx.android.synthetic.main.activity_table_list.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.json.JSONObject
import java.net.URL
import java.util.*

class TableListActivity : AppCompatActivity(), TableListFragment.OnTableSelectedListener {

    enum class VIEW_INDEX(val index: Int) {
        LOADING(0),
        TABLES(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_list)

        setupViewSwitcher()
        loadMenu()

        // Comprobamos el layout
        if (findViewById<View>(R.id.fragment_table_list) != null) {
            //Comprobamos el fragment
            if (fragmentManager.findFragmentById(R.id.fragment_table_list) == null) {
                val fragment = TableListFragment.newInstance()
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_table_list, fragment)
                        .commit()
            }
        }
    }

    private fun setupViewSwitcher(){
        view_switcher.setInAnimation(this, android.R.anim.fade_in)
        view_switcher.setOutAnimation(this, android.R.anim.fade_out)
    }

    private fun loadMenu() {
        if (MenuList.courses.size == 0) {
            Log.v("TAG", "CARGAR EL MENU")
            view_switcher.displayedChild = VIEW_INDEX.LOADING.index

            val activity = this

            async(UI) {
                val menuListDownloader: Deferred<List<Course>?> = bg {
                    downloadMenuList()
                }

                val downloadedMenuList = menuListDownloader.await()
                view_switcher.displayedChild = VIEW_INDEX.TABLES.index

                if (downloadedMenuList != null) {
                    // MenuList.courses.addAll(downloadedMenuList)
                    MenuList.courses += downloadedMenuList
                } else {
                    AlertDialog.Builder(activity)
                            .setTitle("Error en la descarga")
                            .setMessage("No se puede descargar el menú")
                            .setPositiveButton("Reintentar", { dialog, _ ->
                                dialog.dismiss()
                                loadMenu()
                            })
                            .setNegativeButton("Salir", { _, _ -> activity.finish() })
                            .show()
                }
            }

        } else {
            Log.v("TAG", "MENU CARGADO")
            view_switcher.displayedChild = VIEW_INDEX.TABLES.index
        }
    }

    private fun downloadMenuList(): List<Course>? {
        try {

            Thread.sleep(1000)

            // Descarga del menú
            val url = URL("http://www.mocky.io/v2/5a11de582c00007201ace4ce")
            val jsonString = Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next()

            // Analizamos los datos descargados
            val jsonRoot = JSONObject(jsonString)
            val myMenu = jsonRoot.getJSONArray("menu")

            // Nos creamos la lista que vamos a ir rellenando
            val menuItems = mutableListOf<Course>()

            //Recorremos el menú del objeto JSON
            for (itemIndex in 0 until myMenu.length()) {
                val item = myMenu.getJSONObject(itemIndex)
                val name = item.getString("name")
                val price = item.getString("price").toFloat()
                val image = item.getString("image")
                val description = "Some information about ${item.getString("name")}"

                menuItems.add(Course(name, price, image, description))
            }

            return menuItems

        } catch (ex: Exception) {
            ex.printStackTrace() // Revisar permisos de internet???
        }

        return null
    }

    override fun onTableSelected(table: Table, position: Int) {
        Log.v("LOG", "Se ha pulsado la posición: ${position}")
        startActivity(TableActivity.intent(this, position))
    }

}
