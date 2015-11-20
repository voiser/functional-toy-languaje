// Generated from src/main/antlr/Grammar.g4 by ANTLR 4.5.1
package ast2;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link GrammarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface GrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link GrammarParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(GrammarParser.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(GrammarParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(GrammarParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(GrammarParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#fn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFn(GrammarParser.FnContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#fnargpair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFnargpair(GrammarParser.FnargpairContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDef(GrammarParser.DefContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#apply}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitApply(GrammarParser.ApplyContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRef(GrammarParser.RefContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#cond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCond(GrammarParser.CondContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#forward}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForward(GrammarParser.ForwardContext ctx);
	/**
	 * Visit a parse tree produced by {@link GrammarParser#tydef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTydef(GrammarParser.TydefContext ctx);
}