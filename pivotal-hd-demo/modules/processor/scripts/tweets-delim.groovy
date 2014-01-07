import groovy.json.JsonSlurper

def slurper = new JsonSlurper()
def jsonPayload = slurper.parseText(payload)
def fromUser = jsonPayload?.fromUser
def hashTags = jsonPayload?.entities?.hashTags
def followers = jsonPayload?.user?.followersCount
def createdAt = jsonPayload?.createdAt
def languageCode = jsonPayload?.languageCode
def retweetCount = jsonPayload?.retweetCount
def retweet = jsonPayload?.retweet

def result = ""
if (hashTags == null || hashTags.size() == 0) {
  result = result + jsonPayload.id + '\t' + fromUser + '\t' + createdAt + '\t' + '-' + '\t' + followers + '\t' + languageCode + '\t' + retweetCount + '\t' + retweet
} else {
  hashTags.each { tag ->
    if (result.size() > 0) {
      result = result + "\n"
    }
    result = result + jsonPayload.id + '\t' + fromUser + '\t' + createdAt + '\t' + tag.text.replace('\r', ' ').replace('\n', ' ').replace('\t', ' ') + '\t' + followers + '\t' + languageCode + '\t' + retweetCount + '\t' + retweet
  }
}

return result
