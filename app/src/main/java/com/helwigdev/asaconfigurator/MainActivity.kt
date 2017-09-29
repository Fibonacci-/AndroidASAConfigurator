package com.helwigdev.asaconfigurator

import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    var enabledView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showIspViews(false)

        //keep track of which view is selected
        //listen for clicks on the dialup & dsl buttons
        b_dialup.setOnClickListener(View.OnClickListener { view ->
            //set the highlight color
            b_dialup.background.setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY)
            //clear the color from the other buttons
            b_dsl.background.clearColorFilter()
            //set the tracking variable
            enabledView = b_dialup
            //toggle the isp views for ppoe login
            showIspViews(true)
        })
        b_dsl.setOnClickListener(View.OnClickListener { view ->
            b_dsl.background.setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY)
            b_dialup.background.clearColorFilter()
            enabledView = b_dsl
            showIspViews(false)
        })
        cb_ssh.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                tv_notelnet.visibility = View.GONE
            } else {
                //yell at the user
                tv_notelnet.visibility = View.VISIBLE
            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.m_save -> saveConfig()
            else -> onOptionsItemSelected(item)
        }
    }

    fun saveConfig(): Boolean {
        //TODO export/write config to somewhere
        var config = ""
        when (enabledView) {
            null -> {
                toast("Select dial-up or DSL")
                return false
            }
            b_dialup -> config = "######################################################" + "\r\n" +
                    "############## DIALUP SETUP ##########################" + "\r\n" +
                    "" + "\r\n" +
                    "hostname " + et_hostname.text + "\r\n" +
                    "enable password " + et_enablepass.text + " encrypted" + "\r\n" +
                    "interface Ethernet0/0" + "\r\n" +
                    "switchport access vlan 2" + "\r\n" +
                    "interface vlan 1" + "\r\n" +
                    " nameif inside" + "\r\n" +
                    " security-level 100" + "\r\n" +
                    " ip address " + et_internalip.text + " " + et_internalnetmask.text + "\r\n" +
                    "    " + "\r\n" +
                    "vpdn group " + et_ispname.text + "-pppoe request dialout pppoe" + "\r\n" +
                    "vpdn group " + et_ispname.text + "-pppoe localname " + et_ppoeuser.text + "\r\n" +
                    "vpdn group " + et_ispname.text + "-pppoe ppp authentication pap" + "\r\n" +
                    "vpdn username " + et_ppoeuser.text + "password " + et_ppoepass.text + "\r\n" +
                    "interface vlan 2" + "\r\n" +
                    " nameif outside" + "\r\n" +
                    " security-level 0" + "\r\n" +
                    " pppoe client vpdn group " + et_ispname.text + "-pppoe" + "\r\n" +
                    " ip address pppoe setroute" + "\r\n" +
                    "exit" + "\r\n" +
                    "dhcpd auto_config outside" + "\r\n" +
                    "" + "\r\n" +
                    "############# END DIALUP SETUP #######################" + "\r\n" +
                    "######################################################" + "\r\n"
            b_dsl -> {
                config = "######################################################" + "\r\n" +
                "################# DSL SETUP ##########################" + "\r\n" +
                "" + "\r\n" +
                "hostname " + et_hostname.text + "\r\n" +
                "enable password " + et_enablepass.text + " encrypted" + "\r\n" +
                "interface Ethernet0/0" + "\r\n" +
                "switchport access vlan 2" + "\r\n" +
                "interface vlan 1" + "\r\n" +
                " nameif inside" + "\r\n" +
                " security-level 100" + "\r\n" +
                " ip address " + et_internalip.text + " " + et_internalnetmask.text + "\r\n" +
                "    " + "\r\n" +
                "interface vlan 2" + "\r\n" +
                " nameif outside" + "\r\n" +
                " security-level 0" + "\r\n" +
                " ip address " + et_externalIP.text + "\r\n" +
                "" + "\r\n" +
                "############# END DIALUP SETUP #######################" + "\r\n" +
                "######################################################" + "\r\n"
            }
        }
        config += "object network obj_any" + "\r\n" +
        " subnet 0.0.0.0 0.0.0.0" + "\r\n" +
        "object network obj_internal" + "\r\n" +
        " subnet " + et_internalip.text + " " + et_internalnetmask.text + "\r\n" +
        "object network obj_external" + "\r\n" +
        " subnet " + et_externalIP.text + "\r\n" +
        "######################################################" + "\r\n" +
        "############## SSH SETUP #############################" + "\r\n" +
        "" + "\r\n"

        if(cb_ssh.isChecked){
            config += "aaa authentication ssh console LOCAL" + "\r\n" +
            "ssh 0.0.0.0 0.0.0.0 outside" + "\r\n" +
            "ssh timeout 5" + "\r\n" +
            "ssh version 2" + "\r\n" +
            "ssh key-exchange group dh-group1-sha1" + "\r\n" +
            "console timeout 0" + "\r\n"
        }

        config += "" + "\r\n" +
        "############# END SSH SETUP #######################" + "\r\n" +
        "######################################################" + "\r\n"
        alert(config){
            title = "Config"
            positiveButton("OK"){}
        }.show()
        return true
    }

    fun showIspViews(ispViewState: Boolean) {
        if (ispViewState) {
            //setting all isp views to gone
            et_ispname.visibility = View.VISIBLE
            et_ppoeuser.visibility = View.VISIBLE
            et_ppoepass.visibility = View.VISIBLE
        } else {
            et_ispname.visibility = View.GONE
            et_ppoeuser.visibility = View.GONE
            et_ppoepass.visibility = View.GONE
        }
    }
}
