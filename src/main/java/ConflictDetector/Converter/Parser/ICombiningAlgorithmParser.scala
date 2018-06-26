package ConflictDetector.Converter.Parser

import XACMLParser.ParseException
import ConflictDetector.Converter.SyntaxTree._

trait ICombiningAlgorithmParser[T <: TTree]{

  val AlgIdAttribute:String
  val PermitOverride:CombiningAlgorithm
  val DenyOverride:CombiningAlgorithm
  val DenyUnlessPermit:CombiningAlgorithm
  val PermitUnlessDeny:CombiningAlgorithm
  val NoSuchAlgorithmID:String
  def parseCombiningAlgorithm(combAlg: CombiningAlgorithm, terms: T*): Combine[T] =
    combAlg match {
      case PermitOverride   =>  PO(terms:_*)
      case DenyOverride     =>  DO(terms:_*)
      case PermitUnlessDeny =>  DuP(terms:_*)
      case DenyUnlessPermit =>  PuD(terms:_*)
      case _ => throw ParseException(NoSuchAlgorithmID)
    }
}