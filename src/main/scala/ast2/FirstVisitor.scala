package ast2

import scala.collection.JavaConverters._
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream

/**
 * @author david
 */

class FirstVisitor(filename: String) extends GrammarBaseVisitor[Node] {

  def fill[T <: Node](n: T, c: ParserRuleContext) = {
    val line = c.start.getLine
    val column = c.start.getCharPositionInLine
    n.filename = filename
    n.ctx = c
    n
  }
  
  override def visitExpression(ctx: GrammarParser.ExpressionContext) = {
    if (ctx.expression() != null) {
      visitExpression(ctx.expression())
    }
    else visitChildren(ctx)
  }
  
  override def visitValue(ctx: GrammarParser.ValueContext) = {
    if (ctx.INTEGER != null) {
      def raw = ctx.INTEGER.getText
      def i = java.lang.Integer.parseInt(raw)
      fill(NInt(i), ctx)
    } 
    else if (ctx.FLOAT != null) {
      def raw = ctx.FLOAT.getText
      def f = java.lang.Float.parseFloat(raw)
      fill(NFloat(f), ctx)
    }
    else if (ctx.STRING != null) {
      fill(NString(ctx.STRING.getText), ctx)
    }
    else {
      throw new ParseException("Can't parse value " + ctx)
    }
  }

  override def visitApply(ctx: GrammarParser.ApplyContext) = {
    def fname = ctx.ID.getText
    def params= ctx.expression().asScala.toList.map { visitExpression(_) }
    fill(NApply(fname, params), ctx)
  }
  
  override def visitDef(ctx: GrammarParser.DefContext) = {
    def ident = ctx.ID.getText;
    def expr = visitExpression(ctx.expression());
    fill(NDef(ident, expr), ctx)
  }
  
  override def visitRef(ctx: GrammarParser.RefContext) = {
    def ident = ctx.ID.getText
    fill(NRef(ident), ctx)
  }
  
  override def visitFnargpair(ctx: GrammarParser.FnargpairContext) = {
    def ident = ctx.ID.getText
    if (ctx.CLASSID != null) fill(NFnArg(ident, KlassConst(ctx.CLASSID.getText)), ctx)
    else fill(NFnArg(ident, KlassVar("T")), ctx)
  }
  
  override def visitFn(ctx: GrammarParser.FnContext) = {
    def expr = visitBlock(ctx.block())
    def args = ctx.fnargpair()
    if (args == null || args.size == 0) {
      fill(NFn(List(), expr), ctx)
    }
    else {
      def params = args.asScala.toList.map { visitFnargpair(_).asInstanceOf[NFnArg] }
      fill(NFn(params, expr), ctx)
    }
  }
  
  override def visitCond(ctx: GrammarParser.CondContext) = {
    def cond = visitExpression(ctx.condition)
    def exptrue = visitExpression(ctx.exptrue)
    def expfalse = visitExpression(ctx.expfalse)
    fill(NIf(cond, exptrue, expfalse), ctx)
  }
  
  override def visitBlock(ctx: GrammarParser.BlockContext) = {
    fill(NBlock(ctx.expression().asScala.toList.map { visitExpression(_) }), ctx)
  }
  
  override def visitFile(ctx: GrammarParser.FileContext) = {
    val f = fill(NFn(List(), visitBlock(ctx.block())), ctx)
    val imports = ctx.imp().asScala.toList.map { x => 
      val realname = x.IMPORT.getText
      val alias = 
        if (x.alias != null) x.alias.getText
        else realname.split("\\.").last
      (realname, alias)
    }
    fill(NModule(ctx.name.getText, imports, f), ctx)
  }
  
  override def visitForward(ctx: GrammarParser.ForwardContext) = {
    val name = ctx.ID().getText
    val tydef = ctx.ty.getText
    val lexer = new TypegrammarLexer(new ANTLRInputStream(tydef))
    val parser = new TypegrammarParser(new CommonTokenStream(lexer))
    val cst = parser.ty()
    val gty = new TypeVisitor().visitTy(cst)
    fill(NForward(name, gty), ctx)
  }
  
  override def visitBinexp(ctx: GrammarParser.BinexpContext) = {
    if (ctx.binary() != null) {
      visitBinary(ctx.binary())
    }
    else visitChildren(ctx)
  }
  
  override def visitBinary(ctx: GrammarParser.BinaryContext) = {
    val left = 
      if (ctx.xleft != null) visitBinexp(ctx.xleft)
      else visitBinary(ctx.bleft)
      
    val right = visitBinexp(ctx.right)
    val op = ctx.op.getText
    val fname = op match {
      case "+" => "add"
      case "-" => "sub"
      case "*" => "times"
      case "/" => "div"
      case "==" => "eq"
    }
    fill(NApply(fname, List(left, right)), ctx)
  }
  
  override def visitList(ctx: GrammarParser.ListContext) = {
    val exprs = ctx.expression().asScala.toList    
    def cons(exprs: List[GrammarParser.ExpressionContext]) : Node = exprs match {
      case List(a) => fill(NApply("list", List(visitExpression(a))), ctx)
      case x :: xs => fill(NApply("cons", List(visitExpression(x), cons(xs))), ctx)
    }
    cons(exprs)
  }
  
  override def visitMap(ctx: GrammarParser.MapContext) = {
    val pairs = ctx.mappair().asScala.toList
    def cons(exprs: List[GrammarParser.MappairContext]) : Node = exprs match {
      case List(a) => fill(NApply("dict", List(visitExpression(a.mapkey), visitExpression(a.mapvalue))), ctx)
      case x :: xs => fill(NApply("extend", List(visitExpression(x.mapkey), visitExpression(x.mapvalue), cons(xs))), ctx)
    }
    cons(pairs)
  }
}

