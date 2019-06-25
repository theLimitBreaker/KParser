package io.kaendagger.kalcandroid.ui.calcScreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.HorizontalScrollView
import io.kaen.dagger.BadSyntaxException
import io.kaen.dagger.DomainException
import io.kaen.dagger.ExpressionParser

import kotlinx.android.synthetic.main.activity_main.*
import io.kaendagger.kalcandroid.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {

    private val expressionHolder = StringBuilder()
    private var lastAppendedData = ""
    private val textSizesSP = arrayOf(70.0f, 60.0f, 50.0f, 40.0f)
    private var currentTextSizeSP = textSizesSP[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        setUpTvDisplay()
        etDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentTextSizeSP)
        val calcPadPagerAdaper = CalcpadPagerAdaper(
            supportFragmentManager,
            object : OnCalcBtnClickListener {
                override fun onCalcBtnClick(data: String) {
                    adjustExpressionDisplay()
                    when (data) {
                        "=" -> showResult()
                        "C" -> singleCharClearAction()
                        else -> displayExpression(data)
                    }
                }
            })

        keypadPager.apply {
            offscreenPageLimit = 2
            adapter = calcPadPagerAdaper
            setPageTransformer(true, ZoomOutPageTransformer())
        }
    }

    private fun singleCharClearAction() {
        val removeIndex = expressionHolder.length - lastAppendedData.length
        expressionHolder.delete(removeIndex, expressionHolder.length)
        etDisplay.text = expressionHolder.toString()
    }

    private fun displayExpression(extraData: String) {
        expressionHolder.append(extraData)
        lastAppendedData = extraData
        etDisplay.text = expressionHolder.toString()
    }

    private fun adjustExpressionDisplay() {
        val scrollViewWidth = displayScrollView.width
        val tvDisplayWidth = etDisplay.width
        if (tvDisplayWidth > scrollViewWidth) {
            adjustTextSize()
            etDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentTextSizeSP)
        }
        smoothScrollToEnd()
    }

    private fun adjustTextSize() {
        currentTextSizeSP = when (currentTextSizeSP) {
            textSizesSP[0] -> textSizesSP[1]
            textSizesSP[1] -> textSizesSP[2]
            textSizesSP[2] -> textSizesSP[3]
            else -> textSizesSP[3]
        }
    }

    private fun showResult() {
        val expressionParser = ExpressionParser().also { it.enableLog(true) }
        val result = try {
            expressionParser.evaluate(expressionHolder.toString())
        } catch (bs: BadSyntaxException) {
            "Bad Syntax"
        } catch (de: DomainException) {
            "Domain Error"
        }
        tvResult.text = result.toString()
    }

    private fun smoothScrollToEnd() {
        GlobalScope.launch {
            delay(100)
            displayScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
        }
    }

}
