package com.example.myapp1.binding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import com.example.myapp1.R

/**
 * A binding class for handling empty state views in a consistent way across the app.
 * This class provides methods to update the empty state UI components.
 */
class EmptyStateViewBinding private constructor(
    private val root: View
) {
    private val emptyIcon: ImageView = root.findViewById(R.id.emptyIcon)
    private val emptyText: TextView = root.findViewById(R.id.emptyText)
    private val actionButton: com.google.android.material.button.MaterialButton = root.findViewById(R.id.actionButton)

    companion object {
        /**
         * Inflates a new instance of EmptyStateViewBinding.
         *
         * @param inflater The LayoutInflater to use for inflation
         * @param parent The parent view to attach to, if any
         * @param attachToParent Whether to attach to the parent
         * @return A new instance of EmptyStateViewBinding
         */
        @JvmStatic
        fun inflate(
            inflater: LayoutInflater,
            parent: ViewGroup?,
            attachToParent: Boolean
        ): EmptyStateViewBinding {
            return EmptyStateViewBinding(
                inflater.inflate(R.layout.layout_empty_state, parent, attachToParent)
            )
        }
    }

    /**
     * Sets the empty state icon.
     *
     * @param iconRes The resource ID of the icon to display
     */
    fun setEmptyIcon(@DrawableRes iconRes: Int) {
        emptyIcon.setImageResource(iconRes)
    }

    /**
     * Sets the empty state text.
     *
     * @param text The text to display
     */
    fun setEmptyText(text: String) {
        emptyText.text = text
    }

    /**
     * Sets the empty state text from a string resource.
     *
     * @param textRes The string resource ID of the text to display
     */
    fun setEmptyText(@StringRes textRes: Int) {
        emptyText.setText(textRes)
    }

    /**
     * Sets the action button text.
     *
     * @param text The text to display on the action button
     */
    fun setActionText(text: String) {
        actionButton.text = text
    }

    /**
     * Sets the action button text from a string resource.
     *
     * @param textRes The string resource ID of the text to display on the action button
     */
    fun setActionText(@StringRes textRes: Int) {
        actionButton.setText(textRes)
    }

    /**
     * Sets a click listener for the action button.
     *
     * @param listener The click listener to set
     */
    fun setOnActionClickListener(listener: View.OnClickListener) {
        actionButton.setOnClickListener(listener)
    }

    /**
     * Sets the visibility of the action button.
     *
     * @param visible Whether the action button should be visible
     */
    fun setActionVisible(visible: Boolean) {
        actionButton.visibility = if (visible) View.VISIBLE else View.GONE
    }

    /**
     * Gets the root view of the empty state.
     */
    val rootView: View
        get() = root
}

/**
 * Binding adapter for setting the empty state icon from XML.
 */
@BindingAdapter("emptyIcon")
fun setEmptyIcon(view: View, @DrawableRes iconRes: Int) {
    view.findViewById<ImageView>(R.id.emptyIcon).setImageResource(iconRes)
}

/**
 * Binding adapter for setting the empty state text from XML.
 */
@BindingAdapter("emptyText")
fun setEmptyText(view: View, @StringRes textRes: Int) {
    view.findViewById<TextView>(R.id.emptyText).setText(textRes)
}

/**
 * Binding adapter for setting the action button text from XML.
 */
@BindingAdapter("actionText")
fun setActionText(view: View, @StringRes textRes: Int) {
    view.findViewById<com.google.android.material.button.MaterialButton>(R.id.actionButton).setText(textRes)
}

/**
 * Binding adapter for setting a click listener on the action button from XML.
 */
@BindingAdapter("onActionClick")
fun setOnActionClickListener(view: View, listener: View.OnClickListener) {
    view.findViewById<View>(R.id.actionButton).setOnClickListener(listener)
}
