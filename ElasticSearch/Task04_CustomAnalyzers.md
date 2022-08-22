### Exercises:
Create a request to the `_analyze` endpoint that tests analysis of text `"I’m a hungry man"` with a custom analyzer. 
This analyzer should use the `standard` tokenizer and the following token filters: `lowercase`, `asciifolding`, `stop` 
(without customization) and the `shingle` (with unigrams and shingle size from 2 to 4). There should be 9 tokens 
in the result.

### Answers:

```javascript
GET _analyze
{
    "tokenizer": "standard",
    "filter": [
    "lowercase",
    "asciifolding",
    "stop",
    {
        "type": "shingle",
        "min_shingle_size": 2,
        "max_shingle_size": 4,
        "output_unigrams": true  // by default it's set to true but I wanted to make it explicit
    }
],
    "text": "I’m a hungry man"
}
```

[Shingle token filter: Configurable parameters](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-shingle-tokenfilter.html#analysis-shingle-tokenfilter-configure-parms)

#####  Result:
```javascript
{
  "tokens": [
    {
      "token": "i'm",
      "start_offset": 0,
      "end_offset": 3,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "i'm _",
      "start_offset": 0,
      "end_offset": 6,
      "type": "shingle",
      "position": 0,
      "positionLength": 2
    },
    {
      "token": "i'm _ hungry",
      "start_offset": 0,
      "end_offset": 12,
      "type": "shingle",
      "position": 0,
      "positionLength": 3
    },
    {
      "token": "i'm _ hungry man",
      "start_offset": 0,
      "end_offset": 16,
      "type": "shingle",
      "position": 0,
      "positionLength": 4
    },
    {
      "token": "_ hungry",
      "start_offset": 6,
      "end_offset": 12,
      "type": "shingle",
      "position": 1,
      "positionLength": 2
    },
    {
      "token": "_ hungry man",
      "start_offset": 6,
      "end_offset": 16,
      "type": "shingle",
      "position": 1,
      "positionLength": 3
    },
    {
      "token": "hungry",
      "start_offset": 6,
      "end_offset": 12,
      "type": "<ALPHANUM>",
      "position": 2
    },
    {
      "token": "hungry man",
      "start_offset": 6,
      "end_offset": 16,
      "type": "shingle",
      "position": 2,
      "positionLength": 2
    },
    {
      "token": "man",
      "start_offset": 13,
      "end_offset": 16,
      "type": "<ALPHANUM>",
      "position": 3
    }
  ]
}
```