package ir.nardana.bubletabbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat

class BubleTabBar: RelativeLayout {
    var backgrounduncolor: Int = -1
    var backgroundtapped: Int = -1
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
    var titlesize: Float = 0f
    var numcolumns: Int = 0
    var typeface: Typeface? = null
    var backgroundPaint = Paint()
    var backgroundtappedPaint = Paint()
    var textpaint = Paint()
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

    fun initial(context: Context?, attrs: AttributeSet?)
    {
        val ta = context!!.theme.obtainStyledAttributes(attrs,R.styleable.BubleTabBar,0,0)
        try {
            this.radiusall = ta.getInteger(R.styleable.BubleTabBar_RadiusAll,0)
            this.cornerradiusTopLeft = ta.getInteger(R.styleable.BubleTabBar_RadiusTopLeft,0)
            this.cornerradiusTopRight = ta.getInteger(R.styleable.BubleTabBar_RadiusTopRight,0)
            this.cornerradiusBottomLeft = ta.getInteger(R.styleable.BubleTabBar_RadiusBottomLeft,0)
            this.cornerradiusBottomRight = ta.getInteger(R.styleable.BubleTabBar_RadiusBottomRight,0)
            this.boxshadowsize = ta.getInteger(R.styleable.BubleTabBar_BoxShadowSize,-1)
            this.boxshadowsizex = ta.getInteger(R.styleable.BubleTabBar_BoxShadowSizeX,0)
            this.boxshadowsizey = ta.getInteger(R.styleable.BubleTabBar_BoxShadowSizeY,0)
            this.titlesize = ta.getDimension(R.styleable.BubleTabBar_TitleSize,(18 * resources.displayMetrics.density))
            this.backgrounduncolor = ta.getColor(R.styleable.BubleTabBar_BackgroundColor,ContextCompat.getColor(this.context,R.color.teal_200))
            this.boxshadowcolor = ta.getColor(R.styleable.BubleTabBar_BoxShadowColor,
                this.backgrounduncolor)
            this.backgroundtapped = ta.getColor(R.styleable.BubleTabBar_BackgroundTapped,
                ContextCompat.getColor(this.context,R.color.white))
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
        this.backgroundPaint.style = Paint.Style.FILL_AND_STROKE
        this.backgroundPaint.color = this.backgrounduncolor
        this.backgroundPaint.strokeWidth = (2 * resources.displayMetrics.density)
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
        pathbackground.addRoundRect(rectfbackground,corners, Path.Direction.CW)
        canvas!!.drawPath(pathbackground,this.backgroundPaint)
        if(this.listcontainernums.size > 0)
        {
            this.backgroundtappedPaint.style = Paint.Style.FILL
            this.backgroundtappedPaint.color = backgroundtapped
            if(this.numcolumns > 1)
            {
                val eachcontainerwidth = (this.layoutwidth / this.numcolumns)
                for(z in 0 until this.numcolumns){
                    val startcontainer = if(z == 0) z * eachcontainerwidth else z * eachcontainerwidth
                    val endcontainer = startcontainer + eachcontainerwidth
                    this.listcontainernums.get(z).startwidth = startcontainer.toFloat()
                    this.listcontainernums.get(z).endwidth = endcontainer.toFloat()
                    this.listcontainernums.get(z).height = this.layoutheight.toFloat()
                    if(z < this.numcolumns)
                    {
                        val recttemp  = RectF(endcontainer.toFloat(),0f,(endcontainer).toFloat(),this.layoutheight.toFloat())
                        canvas!!.drawRect(recttemp,this.backgroundtappedPaint)
                    }
                }
            }
            for(w in 0 until this.listcontainernums.size){
                val it = this.listcontainernums.get(w)
                if(it.selected)
                {
                    val paintbackgroundselected = Paint()
                    paintbackgroundselected.style = Paint.Style.FILL
                    paintbackgroundselected.color = this.backgroundtapped
                    val rectselecteditem = RectF(it.startwidth + 5f,5f,it.endwidth - 5f,it.height - 5f)
                    val corners: FloatArray
                    if(this.radiusall > 0)
                    {
                        corners = floatArrayOf(this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat(),this.radiusall.toFloat())
                    }
                    else
                    {
                        corners = floatArrayOf(this.cornerradiusTopLeft.toFloat(),this.cornerradiusTopLeft.toFloat(),this.cornerradiusTopRight.toFloat(),this.cornerradiusTopRight.toFloat(),this.cornerradiusBottomRight.toFloat(),this.cornerradiusBottomRight.toFloat(),this.cornerradiusBottomLeft.toFloat(),this.cornerradiusBottomLeft.toFloat())
                    }
                    var temppathbackground = Path()
                    temppathbackground.addRoundRect(rectselecteditem,corners, Path.Direction.CW)
                    canvas!!.drawPath(temppathbackground,paintbackgroundselected)
                }
                if(this.typeface != null) this.textpaint.setTypeface(this.typeface)
                this.textpaint.textSize = this.titlesize
                this.textpaint.color = if(it.selected) this.backgrounduncolor else this.backgroundtapped
                this.textpaint.textAlign = Paint.Align.CENTER
                val positionx = (((it.endwidth - it.startwidth) / 2) + it.startwidth)
                val positiony = (it.height / 2) - ((textpaint.descent() + textpaint.ascent()) / 2)
                canvas!!.drawText(it.title,positionx,positiony,this.textpaint)
            }
        }
    }

    fun getBackgroundUnColor(): Int
    {
        return this.backgrounduncolor
    }

    fun getBackgroundTapped(): Int
    {
        return this.backgroundtapped
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

    fun setBackgroundUnColor(value: Int)
    {
        this.backgrounduncolor = value
        refresh()
    }

    fun setBackgroundTapped(value: Int)
    {
        this.backgroundtapped = value
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