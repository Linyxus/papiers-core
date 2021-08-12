package papiers.tools

import org.json4s._


trait JsonValue:
  def select(value: JValue, path: List[String]): Option[JValue] = path match
    case Nil => Some(value)
    case x :: xs => value match
      case JObject(fields) =>
        fields.find { f => f._1 == x } flatMap { (_, v) => select(v, xs) }
      case _ => None

object JsonValue extends JsonValue
