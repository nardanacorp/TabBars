package ir.nardana.bubletabbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat

class BubleTabBar: RelativeLayout {
    private var backgrounduncolor: Int = -1
    private var backgroundtapped: Int = -1
    private var colortitletapped: Int = -1
    private var radiusall: Int = -1
    private var cornerradiusTopLeft: Int = -1
    private var cornerradiusTopRight: Int = -1
    private var cornerradiusBottomLeft: Int = -1
    private var cornerradiusBottomRight: Int = -1
    private var layoutwidth: Int = -1
    private var layoutheight: Int = -1
    private var boxshadowsize: Int = -1
    private var boxshadowsizex: Int = 0
    private var boxshadowsizey: Int = 0
    private var boxshadowcolor: Int = -1
    private var titlesize: Float = 0f
    private var numcolumns: Int = 0
    private var typeface: Typeface? = null
    private var backgroundPaint = Paint()
    private var backgroundtappedPaint = Paint()
    private var textpaint = Paint()
    private var listcontainernums: MutableList<ContainerNums> = mutableListOf()
    private var indextapped: Int = 0
    private var status_setlist = false

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
            this.colortitletapped = ta.getColor(R.styleable.BubleTabBar_ColorTitleTapped, 0)
        }finally {
            ta.recycle()
        }
        setWillNotDraw(false)
        if(!this.status_setlist) initlist()
    }

    private fun initlist()
    {
        val mutablelist = mutableListOf<String>("First","Second")
        this.setListTabBar(mutablelist)
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
        val totalpaddingleft = if(paddingLeft.toFloat() == 0f) 5f else paddingLeft.toFloat()
        val totalpaddingright = if(paddingRight.toFloat() == 0f) 5f else paddingRight.toFloat()
        val totalpaddingtop = if(paddingTop.toFloat() == 0f) 5f else paddingTop.toFloat()
        val totalpaddingbottom = if(paddingBottom.toFloat() == 0f) 5f else paddingBottom.toFloat()
        if(this.boxshadowsize > -1)
        {
            this.backgroundPaint.setShadowLayer((this.boxshadowsize * resources.displayMetrics.density),this.boxshadowsizex.toFloat(),this.boxshadowsizey.toFloat(),this.boxshadowcolor)
        }
        val rectfbackground = RectF(totalpaddingleft,totalpaddingtop,this.layoutwidth.toFloat() - totalpaddingright,this.layoutheight.toFloat() - totalpaddingbottom)
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
                    val rectselecteditem = RectF(it.startwidth + totalpaddingleft,totalpaddingtop,it.endwidth - totalpaddingright,it.height - totalpaddingbottom)
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
                this.textpaint.color = if(it.selected) if(this.colortitletapped == 0) this.backgrounduncolor else this.colortitletapped else this.backgroundtapped
                this.textpaint.textAlign = Paint.Align.CENTER
                val positionx = (((it.endwidth - it.startwidth) / 2) + it.startwidth)
                val positiony = (it.height / 2) - ((textpaint.descent() + textpaint.ascent()) / 2)
                canvas!!.drawText(it.title,positionx,positiony,this.textpaint)
            }
        }
    }

    fun getColorTitleTapped(): Int
    {
        return this.colortitletapped
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
        this.listcontainernums.removeAll(this.listcontainernums)
        this.numcolumns = value.size
        value.forEach{
            var temps = ContainerNums()
            temps.title = it
            this.listcontainernums.add(temps)
        }
        this.status_setlist = true
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

    fun setColorTitleTapped(value: Int)
    {
        this.colortitletapped = value
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