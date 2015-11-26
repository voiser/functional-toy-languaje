package ast2

/**
 * @author david
 */

/**
 * Models a single function 
 */
class CompilationUnitFunction(
    val unit: CompilationUnit, 
    val root: NFn,
    val name: String, 
    val className: String, 
    val slashedName: String, 
    val constants: List[(String, Node)], 
    val params: List[(String, Node, Int)], 
    val locals: List[(String, Node, Int)], 
    val externs: List[(String, Ty)],
    val captures: List[NRef]
)

/**
 * Models a set of functions
 */
class CompilationUnit(
  val filename: String,
  val module: NModule, 
  val functions: List[Function], 
  val externs: List[Extern]
) {
  
  // Generates all function units contained in this set
  def unitFunctions = {
    functions.map { f =>
      val root = f.function
      val name = f.function.name
      val captures = f.captures
      
      val className = (module.name + "." + name)
      val slashedName = className.replace(".", "/")
    
      val constants: List[(String, Node)] = root.value.children.collect {
        case NDef(name, v : NInt) => (name, v)
        case NDef(name, v : NFloat) => (name, v)
        case NDef(name, v : NString) => (name, v)
      }
      
      val localmap = scala.collection.mutable.Map[String, Int]()
      localmap.put(name, 0) // In the JVM, "this" is local 0 
      var l = 0
      
      val myself = (name, root, 0)
      
      val params: List[(String, Node, Int)] = root.params.collect {
        case x @ NFnArg(name, ty) => 
          l = l + 1
          localmap.put(name, l)
          (name, x, l)
      }
      
      val locals: List[(String, Node, Int)] = myself :: params ++ root.value.children.collect {
        case NDef(name, v : NApply) => 
          l = l + 1
          localmap.put(name, l)
          (name, v, l)
        case NDef(name, v : NFn) => 
          l = l + 1
          localmap.put(name, l)
          (name, v, l)
        case NDef(name, v : NRef) =>
          localmap.get(v.name) match {
            case Some(int) =>
              localmap.put(name, int)
              (name, v, int)
            case None => 
              l = l + 1
              localmap.put(name, l)
              (name, v, l)
          }
      }
      
      val exts: List[(String, Ty)] = externs.find { x => x.function.name == root.name } match {
        case Some(Extern(node, names)) =>
          names.map { name => (name, node.env.get(name).get.tpe) }   
        case None => List()
      }
      
      new CompilationUnitFunction(this, root, name, className, slashedName, constants, params, locals, exts, captures)
    }
  }
}