### Exercises:
1. Create a search request for  the `products` index using a `fuzzy` query (not a `match` query with fuzziness) by the `name.text` field only, that returns all **6 documents**.
2. Create a search request for  the `products` index using only one `match` query with query = `WITH` that returns all **6 documents**. There should be no other parameters in the query.
3. Create a search request for  the `products` index using only one `match` query by the `category_path` field that contains more than 1 word in the query  and returns all **6 documents**.
4. Create a search request  for the `products` index using only one `match` query by the `category_path` field that returns only **2 documents**.
5. Create a search request for  the `products` index using a `match_phrase` query with parameter query = `SILVER METALLIC` that returns **3 documents**.


### Answers:
1.
```javascript
GET products/_search
{
    "query": {
        "fuzzy": {
            "name.text": {
                "value": "shies",
                    "fuzziness": 2
                }
        }
    }
}
```
2.
```javascript
GET products/_search
{
    "query": {
        "match": {
            "description.text": {
                "query": "WITH"
            }
        }
    }
}
```

#### 3-4 Info: `category_path` uses [path_analyzer](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-pathhierarchy-tokenizer.html) for tokenization. In order to check how the path is tokenized use this code snippet: 
```javascript
GET products/_analyze
{
  "analyzer": "path-analyzer",
  "text": "Women/Women's sneakers & shoes/Women's outdoor shoes"
}
```
#### END 3-4 Info

3. 
```javascript
GET products/_search
{
    "query": {
        "match": {
            "category_path": {
                "query": "Women/Women's sneakers & shoes"
            }
        }
    }
}
```
4. 
```javascript
GET products/_search
{
    "query": {
        "match": {
            "category_path": {
                "query": "Women/Women's sneakers & shoes/Women's outdoor shoes"
            }
        }
    }
}
```
5. [Nested query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-nested-query.html) is used in this one
```javascript
GET products/_search
{
    "query": {
        "nested": {
            "path": "skus",
            "query": {
                "match_phrase": {
                    "skus.color.text": {
                        "query": "SILVER METALLIC",
                        "slop": 2
                    }
                }
            }
        }
    }
}
```


