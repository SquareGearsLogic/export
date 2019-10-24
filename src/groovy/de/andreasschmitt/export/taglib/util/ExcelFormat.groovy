package de.andreasschmitt.export.taglib.util

import groovy.util.logging.Log4j
import jxl.biff.DisplayFormat
import jxl.format.Alignment
import jxl.format.Colour
import jxl.format.Orientation
import jxl.format.UnderlineStyle
import jxl.format.VerticalAlignment
import jxl.write.WritableCellFormat
import jxl.write.WritableFont

@Log4j
class ExcelFormat extends WritableCellFormat {
  static final List<String> DEFAULT_FONTS = ['ARIAL', 'TIMES', 'COURIER', 'TAHOMA']
  static final List<String> COLOUR_NAMES = ['UNKNOWN', 'BLACK', 'WHITE', 'DEFAULT_BACKGROUND1',
                                            'DEFAULT_BACKGROUND', 'PALETTE_BLACK', 'RED', 'BRIGHT_GREEN',
                                            'BLUE', 'YELLOW', 'PINK', 'TURQUOISE', 'DARK_RED', 'GREEN',
                                            'DARK_BLUE', 'DARK_YELLOW', 'VIOLET', 'TEAL',
                                            'GREY_25_PERCENT', 'GREY_50_PERCENT', 'PERIWINKLE', 'PLUM2',
                                            'IVORY', 'LIGHT_TURQUOISE2', 'DARK_PURPLE',
                                            'CORAL', 'OCEAN_BLUE', 'ICE_BLUE', 'DARK_BLUE2', 'PINK2',
                                            'YELLOW2', 'TURQOISE2', 'VIOLET2',
                                            'DARK_RED2', 'TEAL2', 'BLUE2', 'SKY_BLUE', 'LIGHT_TURQUOISE',
                                            'LIGHT_GREEN', 'VERY_LIGHT_YELLOW',
                                            'PALE_BLUE', 'ROSE', 'LAVENDER', 'TAN', 'LIGHT_BLUE', 'AQUA',
                                            'LIME', 'GOLD', 'LIGHT_ORANGE', 'ORANGE', 'BLUE_GREY',
                                            'GREY_40_PERCENT', 'DARK_TEAL', 'SEA_GREEN', 'DARK_GREEN',
                                            'OLIVE_GREEN', 'BROWN', 'PLUM', 'INDIGO',
                                            'GREY_80_PERCENT', 'AUTOMATIC', 'GRAY_80', 'GRAY_50', 'GRAY_25']
  static final ORIENTATIONS = ['HORIZONTAL','VERTICAL','PLUS_90','MINUS_90','PLUS_45','MINUS_45','STACKED']
  static final H_ALIGNMENT = ['GENERAL','LEFT','CENTRE','RIGHT','FILL','H_AUTO']
  static final V_ALIGNMENT = ['TOP','MIDDLE','BOTTOM','V_AUTO']

  private Closure formatter = null

  ExcelFormat(WritableFont font) {
    super({ return font ?: new WritableFont(WritableFont.ARIAL) }())
    setup()
  }

  ExcelFormat(WritableFont font, Closure formatter) {
    super({ return font ?: new WritableFont(WritableFont.ARIAL) }())
    this.formatter = formatter
    setup()
  }

  ExcelFormat(String fontName = null) {
    super(getFont(fontName))
    setup()
  }

  ExcelFormat(String fontName, Closure formatter) {
    super(getFont(fontName))
    this.formatter = formatter
    setup()
  }

  ExcelFormat(Closure formatter) {
    super(getFont(null))
    this.formatter = formatter
    setup()
  }

  ExcelFormat(DisplayFormat format) {
    super(getFont(null), format)
    setup()
  }

  ExcelFormat(DisplayFormat format, Closure formatter) {
    super(getFont(null), format)
    this.formatter = formatter
    setup()
  }

  ExcelFormat(WritableFont font, DisplayFormat format) {
    super({ return font ?: new WritableFont(WritableFont.ARIAL) }(), format)
    setup()
  }

  ExcelFormat(WritableFont font, DisplayFormat format, Closure formatter) {
    super({ return font ?: new WritableFont(WritableFont.ARIAL) }(), format)
    this.formatter = formatter
    setup()
  }

  ExcelFormat(String fontName, DisplayFormat format) {
    super(getFont(fontName), format)
    setup()
  }

  ExcelFormat(String fontName, DisplayFormat format, Closure formatter) {
    super(getFont(fontName), format)
    this.formatter = formatter
    setup()
  }

  private void setup(){
    setupColorMixins()
    setupFontMixins()
    setupOrientationsMixins()
    setupHaliganMixins()
    setupValiganMixins()
  }

  private void setupColorMixins() {
    COLOUR_NAMES.each { String name ->
      this.metaClass."${name}" = {
        getDelegate().colour(name)
        return getDelegate()
      }
    }
  }

  private void setupFontMixins() {
    DEFAULT_FONTS.each { String name ->
      this.metaClass."${name}" = {
        getDelegate().font(name)
        return getDelegate()
      }
    }
  }

  private void setupOrientationsMixins() {
    ORIENTATIONS.each { String name ->
      this.metaClass."${name}" = {
        getDelegate().setOrientation(Orientation[name])
        return getDelegate()
      }
    }
  }

  private void setupHaliganMixins() {
    H_ALIGNMENT.each { String name ->
      String realName = name
      if(name=='H_AUTO')
        realName = 'JUSTIFY'
      this.metaClass."${name}" = {
        getDelegate().setAlignment(Alignment[realName])
        return getDelegate()
      }
    }
  }

  private void setupValiganMixins() {
    V_ALIGNMENT.each { String name ->
      String realName = name
      if(name=='MIDDLE')
        realName = 'CENTRE'
      if(name=='V_AUTO')
        realName = 'JUSTIFY'
      this.metaClass."${name}" = {
        getDelegate().setVerticalAlignment(VerticalAlignment[realName])
        return getDelegate()
      }
    }
  }

  private static WritableFont getFont(String fontName) {
    if (!fontName)
      return new WritableFont(WritableFont.ARIAL)
    if (DEFAULT_FONTS.contains(fontName))
      return new WritableFont(WritableFont[fontName])
    return new WritableFont(new WritableFont.FontName(fontName))
  }

  private static WritableFont getFont(WritableFont font) {
    if (font)
      return font
    return new WritableFont(WritableFont.ARIAL)
  }

  ExcelFormat font(String fontName) {
    WritableFont fontRecord = new WritableFont(WritableFont.createFont(getFont(fontName).name), this.font.pointSize,
        new WritableFont.BoldStyle(this.font.boldWeight),
        this.font.italic, this.font.underlineStyle, this.font.colour, this.font.scriptStyle)
    fontRecord.setFontStruckout(this.font.struckout)
    this.setFont(fontRecord)
    return this
  }

  ExcelFormat font(WritableFont font) {
    WritableFont fontRecord = new WritableFont(WritableFont.createFont(getFont(fontName).name), this.font.pointSize,
        new WritableFont.BoldStyle(this.font.boldWeight),
        this.font.italic, this.font.underlineStyle, this.font.colour, this.font.scriptStyle)
    fontRecord.setFontStruckout(this.font.struckout)
    this.setFont(fontRecord)
    return this
  }

  Closure getFormatter() {
    return formatter
  }

  ExcelFormat setFormatter(Closure formatter) {
    this.formatter = formatter
    return this
  }

  ExcelFormat bold() {
    this.font.setBoldStyle(WritableFont.BOLD)
    return this
  }

  ExcelFormat noBold() {
    this.font.setBoldStyle(WritableFont.NO_BOLD)
    return this
  }

  ExcelFormat italic() {
    this.font.setItalic(true)
    return this
  }

  ExcelFormat noItalic() {
    this.font.setItalic(false)
    return this
  }

  ExcelFormat struckout() {
    this.font.setStruckout(true)
    return this
  }

  ExcelFormat noStruckout() {
    this.font.setStruckout(false)
    return this
  }

  ExcelFormat underline() {
    this.font.setUnderlineStyle(UnderlineStyle.NO_UNDERLINE)
    return this
  }

  ExcelFormat underlineSingle() {
    this.font.setUnderlineStyle(UnderlineStyle.SINGLE)
    return this
  }

  ExcelFormat underlineSingleAccounting() {
    this.font.setUnderlineStyle(UnderlineStyle.SINGLE_ACCOUNTING)
    return this
  }

  ExcelFormat underlineDouble() {
    this.font.setUnderlineStyle(UnderlineStyle.DOUBLE)
    return this
  }

  ExcelFormat underlineDoubleAccounting() {
    this.font.setUnderlineStyle(UnderlineStyle.DOUBLE_ACCOUNTING)
    return this
  }

  ExcelFormat noUnderline() {
    this.font.setUnderlineStyle(UnderlineStyle.NO_UNDERLINE)
    return this
  }

  ExcelFormat colour(Colour colour) {
    this.font.setColour(colour)
    return this
  }

  ExcelFormat colour(String colourName) {
    if (COLOUR_NAMES.contains(colourName)) {
      this.font.setColour(Colour[colourName])
      return this
    }
    Colour colour = Colour.allColours.find { it.string == colourName }
    if (colour)
      this.font.setColour(colour)
    return this
  }

  ExcelFormat colour(int magic, String s, int r, int g, int b) {
    (this.font as WritableFont).setColour(colour)
    return this
  }

  ExcelFormat noColour() {
    this.font.setColour(Colour.UNKNOWN)
    return this
  }

  ExcelFormat pointSize(int pointSize) {
    (this.font as WritableFont).setPointSize(pointSize)
    return this
  }

  ExcelFormat defaultSize() {
    (this.font as WritableFont).setPointSize(WritableFont.DEFAULT_POINT_SIZE)
    return this
  }

  ExcelFormat backColor(Colour colour) {
    this.setBackground(colour)
    return this
  }

  ExcelFormat noBackColor() {
    this.setBackground(Colour.WHITE)
    return this
  }

  ExcelFormat wrapText(){
    this.setWrap(true)
    return this
  }

  ExcelFormat noWrapText(){
    this.setWrap(false)
    return this
  }

  ExcelFormat ident(int val){
    this.setIndentation(val)
    return this
  }

  ExcelFormat lock(){
    this.setLocked(true)
    return this
  }

  ExcelFormat unlock(){
    this.setLocked(false)
    return this
  }

  ExcelFormat srink(){
    this.setShrinkToFit(true)
    return this
  }

  ExcelFormat expand(){
    this.setShrinkToFit(false)
    return this
  }


}
