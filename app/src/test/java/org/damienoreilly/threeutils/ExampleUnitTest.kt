package org.damienoreilly.threeutils

import org.junit.Test


class ExampleUnitTest {

    val enterCompetitionErrorResponse: String =
            """
    {
      "message" : "subscriber.offer.limit.reached",
      "status" : 411
    }
  """.trimIndent()

    val badCredentialsResponse: String =
            """
      {
         "error": "invalid_grant",
         "error_description": "Bad credentials"
      }
    """.trimIndent()

    val fatalErrorResponse: String =
            """
    {
      "timestamp": 1539970430277,
      "status": 405,
      "error": "Method Not Allowed",
      "exception": "java.lang.Exception",
      "message": "Something bad happened",
      "path": "/some/path"
    }
  """.trimIndent()

    val usageDetails: String = """
{
  "plan": "Prepay",
  "wheels": [],
  "bars": [],
  "text": [{
    "name": "3 to 3 Calls",
    "expiryText": "Expires 27/04/19",
    "remaining": "2,990"
  }, {
    "name": "Text in Republic of Ireland & EU",
    "expiryText": "Expires 27/04/19",
    "remaining": "2,998"
  }, {
    "name": "Unlimited Data in Republic of Ireland",
    "expiryText": "Expires 27/04/19",
    "remaining": "2,095,058"
  }, {
    "name": "Internet in Republic of Ireland & EU",
    "expiryText": "Expires 27/04/19",
    "remaining": "8,192"
  }, {
    "name": "Internet in Republic of Ireland & EU",
    "expiryText": "In queue",
    "remaining": "500"
  }, {
    "name": "Free cash",
    "expiryText": "Expires 10/05/19",
    "remaining": "€10"
  }, {
    "name": "Free cash",
    "expiryText": "In queue",
    "remaining": "€10"
  }]
}
""".trimIndent()


    @Test
    fun addition_isCorrect() {

    }

}
