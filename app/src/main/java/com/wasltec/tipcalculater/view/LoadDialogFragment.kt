package com.wasltec.tipcalculater.view


import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import com.wasltec.tipcalculater.R
import com.wasltec.tipcalculater.viewmodel.CalculatorViewModel
import kotlinx.android.synthetic.main.saved_tip_calculation_list.view.*

class LoadDialogFragment : DialogFragment() {

    private var loadTipCallback : Callback? = null

    interface Callback{
        fun onTipSelected(name : String)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        loadTipCallback = context as? Callback
    }

    override fun onDetach() {
        super.onDetach()
        loadTipCallback = null
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = context?.let { ctx ->

            AlertDialog.Builder(ctx)
                    .setView(createView(ctx))
                    .setNegativeButton(R.string.action_cancel, null)
                    .create()

        }

        return dialog!!
    }

    private fun createView(ctx: Context) : View {

        val rv = LayoutInflater
                .from(ctx)
                .inflate(R.layout.saved_tip_calculation_list, null)
                .recycler_saved_calculations

        rv.setHasFixedSize(true)
        rv.addItemDecoration(DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL))

        val adapter = TipSummaryAdapter {
            loadTipCallback?.onTipSelected(it.locationName)
            dismiss()
        }
        rv.adapter = adapter

        val vm = ViewModelProviders.of(activity!!).get(CalculatorViewModel::class.java)

        vm.loadSavedTipCalculationSummaries().observe(this, Observer {
            if(it != null) {
                adapter.updateList(it)
            }
        })

        return rv
    }

}