package com.example.emrtdapplication.display.lds1

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.emrtdapplication.CreateView
import com.example.emrtdapplication.EMRTD
import com.example.emrtdapplication.R

object DG5Display : CreateView() {

    /**
     * Dynamically create a view for every biometric information in this file.
     *
     * @param context The context in which to create the view
     * @param parent The parent of the view to create
     */
    @OptIn(ExperimentalStdlibApi::class)
    override fun <T : LinearLayout> createView(context: Context, parent: T) {
        if (EMRTD.ldS1Application.dg5.tlvS == null) return
        for (portrait in EMRTD.ldS1Application.dg5.tlvS) {
            val box = LinearLayout(context)
            box.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (alternate) {
                box.setBackgroundColor(context.resources.getColor(R.color.gray, null))
            } else {
                box.setBackgroundColor(context.resources.getColor(R.color.black, null))
            }
            alternate = !alternate
            parent.addView(box)
            val image = portrait.displayPortrait.imageInputStream.readAllBytes()
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            val view = ImageView(context)
            view.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            view.setImageBitmap(bitmap)
            box.addView(view)
        }
    }
}