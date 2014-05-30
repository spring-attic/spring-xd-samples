import groovy.json.JsonSlurper

def slurper = new JsonSlurper()
def jsonPayload = slurper.parseText(payload)
def fromUser = jsonPayload?.user?.name
def hashTags = jsonPayload?.entities?.hashtags
def followers = jsonPayload?.user?.followers_count
def createdAt = jsonPayload?.created_at
def languageCode = jsonPayload?.lang
def retweetCount = jsonPayload?.retweet_count
def retweet = jsonPayload?.retweeted

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