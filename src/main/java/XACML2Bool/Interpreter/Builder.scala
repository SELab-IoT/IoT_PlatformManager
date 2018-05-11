package XACML2Bool.Interpreter

object Builder {
  def con(terms:String*):String = buildWith("*", terms)
  def dis(terms:String*):String = buildWith("+", terms)
  def neg(n:String):String = "-" + n

  def buildWith(op:String, terms:Seq[String]):String = op + joinAll(terms:_*)

  def wrap(n:String):String = "(" + n + ")"
  def joinAll(n: String*):String = wrap(n.reduce(_+" "+_))
}
