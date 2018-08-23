package mch.helloworld

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.content.DialogInterface
import android.R.string.cancel
import android.content.Context
import android.support.v7.app.AlertDialog
import android.util.Log

class MyDialogFragment : DialogFragment() {

    val TAG = "MyDialogFragment"

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(activity as Context)
        builder.setMessage("message")
                .setPositiveButton("positive", DialogInterface.OnClickListener { dialog, id ->
                    // FIRE ZE MISSILES!
                })
                .setNegativeButton("negative", DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog
                })
        // Create the AlertDialog object and return it
        return builder.create()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)

        Log.d(TAG, "onDismiss: ")

    }
}