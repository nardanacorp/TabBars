package ir.nardana.flattabbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import java.util.ArrayList

class FlatTabBar: RelativeLayout {
    var backgroundpretapped: Int = -1
    var backgroundaftertapped: Int = -1
    var colortitletapped: Int = -1
    var radiusall: Int = -1
    var cornerradiusTopLeft: Int = -1
    var cornerradiusTopRight: Int = -1
    var cornerradiusBottomLeft: Int = -1
    var cornerradiusBottomRight: Int = -1
    var layoutwidth: Int = -1
    var layoutheight: Int = -1
    var boxshadowsize: Int = -1
    var boxshadowsizex: Int = 0
    var boxshadowsizey: Int = 0
    var boxshadowcolor: Int = -1
    var colorstroke: Int = -1
    var strokesize: Float = -1f
    var numcolumns: Int = 0
    var titlesize: Float = 0f
    var typeface: Typeface? = null
    var backgroundPaint = Paint()
    var backgroundpaintmiddle = Paint()
    var textpaint = Paint()
    val widthcolumnseq = 2
    var listcontainernums: MutableList<ContainerNums> = mutableListOf()
    var indextapped: Int = 0
    lateinit var listener: OnSelectedItem

    constructor(context: Context?) : super(context){
        this.listcontainernums = mutableListOf()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initial(context,attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        this.listcontainernums = mutableListOf()
        initial(context,attrs)
    }

    fun initial(context: Context?,attrs: AttributeSet?)
    {
        val ta = context!!.theme.obtainStyledAttributes(attrs,R.styleable.FlatTabBar,0,0)
        try {
            this.radiusall = ta.getInteger(R.styleable.FlatTabBar_RadiusAll,0)
            this.cornerradiusTopLeft = ta.getInteger(R.styleable.FlatTabBar_RadiusTopLeft,0)
            this.cornerradiusTopRight = ta.getInteger(R.styleable.FlatTabBar_RadiusTopRight,0)
            this.cornerradiusBottomLeft = ta.getInteger(R.styleable.FlatTabBar_RadiusBottomLeft,0)
            this.cornerradiusBottomRight = ta.getInteger(R.styleable.FlatTabBar_RadiusBottomRight,0)
            this.boxshadowsize = ta.getInteger(R.styleable.FlatTabBar_BoxShadowSize,-1)
            this.boxshadowsizex = ta.getInteger(R.styleable.FlatTabBar_BoxShadowSizeX,0)
            this.boxshadowsizey = ta.getInteger(R.styleable.FlatTabBar_BoxShadowSizeY,0)
            this.numcolumns = ta.getInteger(R.styleable.FlatTabBar_NumColumns,0)
            this.strokesize = ta.getDimension(R.styleable.FlatTabBar_StrokeSize,(2 * resources.displayMetrics.density))
            this.titlesize = ta.getDimension(R.styleable.FlatTabBar_TitleSize,(18 * resources.displayMetrics.density))
            this.colorstroke = ta.getColor(R.styleable.FlatTabBar_ColorStroke,
                ContextCompat.getColor(context,R.color.teal_200))
            this.boxshadowcolor = ta.getColor(R.styleable.FlatTabBar_BoxShadowColor,
                this.colorstroke)
            this.colortitletapped = ta.getColor(R.styleable.FlatTabBar_ColorTitleTapped,
                ContextCompat.getColor(context,R.color.teal_200))
            this.backgroundpretapped = ta.getColor(R.styleable.FlatTabBar_BackgroundPreTapped,
                ContextCompat.getColor(context,R.color.teal_200))
            this.backgroundaftertapped = ta.getColor(R.styleable.FlatTabBar_BackgroundAfterTapped,
                this.colorstroke)
        }finally {
            ta.recycle()
        }
        setWillNotDraw(false)
    }

    private fun clearselected()
    {
        this.listcontainernums.forEach{
            it.selected = false
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event!!.action == MotionEvent.ACTION_DOWN)
        {
            val xtouched = event!!.x
            for(q in 0 until this.listcontainernums.size)
            {
                var it = this.listcontainernums.get(q)
                if(xtouched >= it.startwidth && xtouched <= it.endwidth)
                {
                    this.clearselected()
                    it.selected = true
                    this.indextapped = q
                    if(this::listener.isInitialized) this.listener.OnTapped(this.indextapped)
                    refresh()
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        this.layoutwidth = this.measuredWidth
        this.layoutheight = this.measuredHeight
        this.backgroundPaint.style = Paint.Style.STROKE
        this.backgroundPaint.color = this.colorstroke
        this.backgroundPaint.strokeWidth = this.strokesize
        if(this.boxshadowsize > -1)
        {
            this.backgroundPaint.setShadowLayer((this.boxshadowsize * resources.displayMetrics.density),this.boxshadowsizex.toFloat(),this.boxshadowsizey.toFloat(),this.boxshadowcolor)
        }
        val rectfbackground = RectF(0f,0f,this.layoutwidth.toFloat(),this.layoutheight.toFloat())
        val corners: FloatArray
        if(this.radiusall > 0)
        {
            corners = floatArrayOf(this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat())
        }
        else
        {
            corners = floatArrayOf(this.cornerradiusTopLeft.toFloat(),this.cornerradiusTopLeft.toFloat(),this.cornerradiusTopRight.toFloat(),this.cornerradiusTopRight.toFloat(),this.cornerradiusBottomRight.toFloat(),this.cornerradiusBottomRight.toFloat(),this.cornerradiusBottomLeft.toFloat(),this.cornerradiusBottomLeft.toFloat())
        }
        var pathbackground = Path()
        pathbackground.addRoundRect(rectfbackground,corners,Path.Direction.CW)
        canvas!!.drawPath(pathbackground,this.backgroundPaint)
        if(this.listcontainernums.size > 0)
        {
            this.backgroundpaintmiddle.style = Paint.Style.FILL
            this.backgroundpaintmiddle.color = this.colorstroke
            if(this.numcolumns > 1)
            {
                val eachcontainerwidth = (this.layoutwidth / this.numcolumns)
                for(z in 0 until this.numcolumns){
                    val startcontainer = if(z == 0) z * eachcontainerwidth else z * eachcontainerwidth + widthcolumnseq
                    val endcontainer = startcontainer + eachcontainerwidth - this.widthcolumnseq
                    this.listcontainernums.get(z).startwidth = startcontainer.toFloat()
                    this.listcontainernums.get(z).endwidth = endcontainer.toFloat()
                    this.listcontainernums.get(z).height = this.layoutheight.toFloat()
                    if(z < this.numcolumns)
                    {
                        val recttemp  = RectF(endcontainer.toFloat(),0f,(endcontainer + widthcolumnseq).toFloat(),this.layoutheight.toFloat())
                        canvas!!.drawRect(recttemp,this.backgroundpaintmiddle)
                    }
                }
            }
            for(w in 0 until this.listcontainernums.size){
                val it = this.listcontainernums.get(w)
                if(it.selected)
                {
                    val paintbackgroundselected = Paint()
                    paintbackgroundselected.style = Paint.Style.FILL
                    paintbackgroundselected.color = this.backgroundaftertapped
                    val rectselecteditem = RectF(it.startwidth,0f,it.endwidth,it.height)
                    val corners: FloatArray
                    if(w == 0)
                    {
                        if(this.radiusall > 0)
                        {
                            corners = floatArrayOf(this.radiusall.toFloat(),this.radiusall.toFloat(),0f,0f,0f,0f,this.radiusall.toFloat(),this.radiusall.toFloat())
                        }
                        else
                        {
                            corners = floatArrayOf(this.cornerradiusTopLeft.toFloat(),this.cornerradiusTopLeft.toFloat(),0f,0f,0f,0f,this.cornerradiusBottomLeft.toFloat(),this.cornerradiusBottomLeft.toFloat())
                        }
                    } else if(w > 0 && w < (this.listcontainernums.size - 1)) {
                        corners = floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f)
                    } else{
                        if(this.radiusall > 0)
                        {
                            corners = floatArrayOf(0f,0f,this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat(),0f,0f)
                        }
                        else
                        {
                            corners = floatArrayOf(0f,0f,this.cornerradiusTopRight.toFloat(),this.cornerradiusTopRight.toFloat(),this.cornerradiusBottomRight.toFloat(),this.cornerradiusBottomRight.toFloat(),0f,0f)
                        }
                    }
                    var temppathbackground = Path()
                    temppathbackground.addRoundRect(rectselecteditem,corners,Path.Direction.CW)
                    canvas!!.drawPath(temppathbackground,paintbackgroundselected)
                }
                if(this.typeface != null) this.textpaint.setTypeface(this.typeface)
                this.textpaint.textSize = this.titlesize
                this.textpaint.color = if(it.selected) ContextCompat.getColor(this.context,R.color.white) else this.colorstroke
                this.textpaint.textAlign = Paint.Align.CENTER
                val positionx = (((it.endwidth - it.startwidth) / 2) + it.startwidth)
                val positiony = (it.height / 2) - ((textpaint.descent() + textpaint.ascent()) / 2)
                canvas!!.drawText(it.title,positionx,positiony,this.textpaint)
            }
        }
    }

    fun getSelectedItem(): Int
    {
        return this.indextapped
    }

    fun getTitleSize(): Float
    {
        return this.titlesize
    }

    fun getListTabBar(): MutableList<String>
    {
        var templist = mutableListOf<String>()
        this.listcontainernums.forEach {
            templist.add(it.title)
        }
        return templist
    }

    fun getNumColumns(): Int
    {
        return this.numcolumns
    }

    fun getColorStroke(): Int
    {
        return this.colorstroke
    }

    fun getStrokeSize(): Float
    {
        return this.strokesize
    }

    fun getBackgroundPreTapped(): Int
    {
        return this.backgroundpretapped
    }

    fun getBackgroundAfterTapped(): Int
    {
        return this.backgroundaftertapped
    }

    fun getColorTitleTapped(): Int
    {
        return this.colortitletapped
    }

    fun getRadiusTopRight(): Int
    {
        return this.cornerradiusTopRight
    }

    fun getRadiusBottomRight(): Int
    {
        return this.cornerradiusBottomRight
    }

    fun getRadiusBottomLeft(): Int
    {
        return this.cornerradiusBottomLeft
    }

    fun getTypeFace(): Typeface?
    {
        return this.typeface!!
    }

    fun getLayoutWidth(): Int
    {
        return this.layoutwidth
    }

    fun getLayoutHeight(): Int
    {
        return this.layoutheight
    }

    fun getBoxShadowColor(): Int
    {
        return this.boxshadowcolor
    }

    fun getBoxShadowSize(): Int
    {
        return this.boxshadowsize
    }

    fun getBoxShadowSizeX(): Int
    {
        return this.boxshadowsizex
    }

    fun getBoxShadowSizeY(): Int
    {
        return this.boxshadowsizey
    }

    fun setRadiusAll(value: Int)
    {
        this.radiusall = value
        refresh()
    }

    fun setTypeFace(value: Typeface)
    {
        this.typeface = value
        refresh()
    }

    fun setRadiusTopLeft(value: Int)
    {
        this.cornerradiusTopLeft = value
        refresh()
    }

    fun setRadiusTopRight(value: Int)
    {
        this.cornerradiusTopRight = value
        refresh()
    }

    fun setRadiusBottomRight(value: Int)
    {
        this.cornerradiusBottomRight = value
        refresh()
    }

    fun setRadiusBottomLeft(value: Int)
    {
        this.cornerradiusBottomLeft = value
        refresh()
    }

    fun setBoxShadowColor(value: Int)
    {
        this.boxshadowcolor = value
        refresh()
    }

    fun setBoxShadowSize(value: Int)
    {
        this.boxshadowsize = value
        refresh()
    }

    fun setBoxShadowSizeX(value: Int)
    {
        this.boxshadowsizex = value
        refresh()
    }

    fun setBoxShadowSizeY(value: Int)
    {
        this.boxshadowsizey = value
        refresh()
    }

    fun setLayoutWidth(value: Int)
    {
        this.layoutwidth = value
        refresh()
    }

    fun setLayoutHeight(value: Int)
    {
        this.layoutheight = value
        refresh()
    }

    fun setColorStroke(value: Int)
    {
        this.colorstroke = value
        refresh()
    }

    fun setStrokeSize(value: Float)
    {
        this.strokesize = value
        refresh()
    }

    fun setTitleSize(value: Float)
    {
        this.titlesize = value
        refresh()
    }

    fun setListTabBar(value: MutableList<String>)
    {
        this.numcolumns = value.size
        value.forEach{
            var temps = ContainerNums()
            temps.title = it
            this.listcontainernums.add(temps)
        }
        if(this.numcolumns > 0) this.listcontainernums.get(0).selected = true
        refresh()
    }

    fun setColorTitleTapped(value: Int)
    {
        this.colortitletapped = value
        refresh()
    }

    fun setSelectedItem(value: Int)
    {
        if(value > -1 && value < this.listcontainernums.size)
        {
            this.clearselected()
            var it = this.listcontainernums.get(value)
            it.selected = true
            this.indextapped = value
            refresh()
        }
    }

    fun setOnUpdaterListener(onselecteditem: OnSelectedItem)
    {
        this.listener = onselecteditem
        refresh()
    }

    fun refresh()
    {
        invalidate()
        requestLayout()
    }

    interface OnSelectedItem {
        fun OnTapped(index: Int)
    }
}