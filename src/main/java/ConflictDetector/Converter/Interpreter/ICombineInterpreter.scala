package ConflictDetector.Converter.Interpreter

import ConflictDetector.Converter.SyntaxTree._

trait ICombineInterpreter[T <: TTree] {

  def True:Mode
  def interpretPO(terms: T*):String
  def interpretDO(terms: T*):String
  def interpretDuP(terms: T*):String
  def interpretPuD(terms: T*):String

  def interpretCombineAlgorithm(terms: Combine[T]):String =
    terms match {
      case PO(ps@_*) => interpretPO(ps: _*)
      case DO(ps@_*) => interpretDO(ps: _*)
      case DuP(ps@_*) => interpretDuP(ps: _*)
      case PuD(ps@_*) => interpretPuD(ps: _*)
    }
}