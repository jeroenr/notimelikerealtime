package utils

import scala.concurrent.duration.FiniteDuration

object Implicits {
    implicit def fromFiniteDurationToMillis(duration:FiniteDuration): Long = {
      duration.toMillis
    }
}
