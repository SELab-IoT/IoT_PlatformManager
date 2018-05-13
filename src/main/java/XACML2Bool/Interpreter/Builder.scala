package XACML2Bool.Interpreter

object Builder {
  def con(terms:String*):String =
    if(terms contains FALSE) FALSE
    else buildWith(AND, terms filter TRUE.equals)

  def dis(terms:String*):String =
    if(terms contains TRUE) TRUE
    else buildWith(OR, terms filter FALSE.equals)

  def neg(n:String):String =
    if(n equals TRUE) FALSE
    else if(n equals FALSE) TRUE
    else if(n startsWith NOT) n replaceFirst (NOT, "")
    else NOT + n

  def buildWith(op:String, terms:Seq[String]):String = op + joinAll(terms:_*)

  def wrap(n:String):String = "(" + n + ")"
  def joinAll(n: String*):String = wrap(n reduce (_+" "+_))

  def AND = "*"
  def OR = "+"
  def NOT = "-"

  def TRUE = "$TRUE$"
  def FALSE = "$FALSE$"
}
