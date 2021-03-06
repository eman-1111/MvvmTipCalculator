package com.wasltec.tipcalculater.viewmodel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.databinding.BaseObservable
import com.wasltec.tipcalculater.R
import com.wasltec.tipcalculater.model.Calculator
import com.wasltec.tipcalculater.model.TipCalculation


class CalculatorViewModel @JvmOverloads constructor( app: Application,val calculator: Calculator = Calculator()) : ObservableViewModel(app){

    var inputCheckAmount = ""
    var inputTipPercentage = ""

    private var lastTipCalculation = TipCalculation()

    val outputCheckAmount get() = getApplication<Application>().getString(R.string.dollar_amount ,lastTipCalculation.checkAmount)
    val OutputTipAmount get()= getApplication<Application>().getString(R.string.dollar_amount ,lastTipCalculation.tipAmount)
    val OutputGrandDollarAmount get()= getApplication<Application>().getString(R.string.dollar_amount ,lastTipCalculation.grandTotal)
    val locationName get() = lastTipCalculation.locationName


    init {
        updateOutputs(TipCalculation())
    }

    private fun updateOutputs(tc: TipCalculation) {
        lastTipCalculation = tc
        notifyChange()
    }


    fun calculateTip() {
        val checkAmount = inputCheckAmount.toDoubleOrNull()
        val tipPct = inputTipPercentage.toIntOrNull()

        if (checkAmount != null && tipPct != null) {
            updateOutputs(calculator.calculateTip(checkAmount, tipPct))
        }
    }

    fun clearInputs() {
        inputCheckAmount = "0.00"
        inputTipPercentage = "0"
        notifyChange()
    }

    fun saveCurrentTip(name : String){
        val tipToSave = lastTipCalculation.copy(locationName = name)
        calculator.saveTipCalculation(tipToSave)
        updateOutputs(tipToSave)
    }


    fun loadSavedTipCalculationSummaries() : LiveData<List<TipCalculationSummaryItem>> {
        return Transformations.map(calculator.loadSavedTipCalculations(), { tipCalculationObjects ->
            tipCalculationObjects.map {
                TipCalculationSummaryItem(it.locationName,
                        getApplication<Application>().getString(R.string.dollar_amount, it.grandTotal))
            }
        })
    }


    fun loadTipCalculation(name: String) {

        val tc = calculator.loadTipCalculationByLocationName(name)

        if (tc != null) {
            inputCheckAmount = tc.checkAmount.toString()
            inputTipPercentage = tc.tipPact.toString()

            updateOutputs(tc)
            notifyChange()
        }
    }
}