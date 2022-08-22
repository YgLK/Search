### Exercises:
1. Create a search request to the products index using a match_all query that returns only 3 documents.
2. Create a search request to the products index using a range query by the price field that returns only 2 documents with _id = 3 and 5.

In the following query:

```javascript
GET products/_search
{
"query": {
    "bool": {
      "filter": {"exists": {"field": "id"}},
      "should": [
        {"term": {"id": 1}},
        {"term": {"id": 2}},
        {"term": {"id": 3}}
      ],
      "minimum_should_match": ...
    }}}
```

set minimum_should_match:

3. To positive value, so that this request returns 0 documents
4. To negative value, so that this request returns 3 documents
5. To negative value, so that this request returns 0 documents
6. To negative value, so that this request returns 6 documents
7. To percentage negative value > -95% and < -5%, so that this request returns 3 documents.


### Answers:
1.
```javascript
GET products/_search
{
    "query": {
    "match_all": {}
},
    "size": 3
}
```
2.
```javascript
GET products/_search
{
    "query": {
        "range":{
            "price":{
                "gte": 105,
                "lte": 110
            }
        }
    }
}
```
3.
```javascript
  "minimum_should_match": 2
```
4.
```javascript
  "minimum_should_match": -2
```
5.
```javascript
  "minimum_should_match": -1
```
6.
```javascript
  "minimum_should_match": -3
```
7.
```javascript
  "minimum_should_match": "-70%"
// because there are 3 should clasues -> flor(3 * 0.7) = floor(2.1) = 2 -> 2 clauses doesnt need to be satisfied -> 3-2=1 should clause is mandatory
```