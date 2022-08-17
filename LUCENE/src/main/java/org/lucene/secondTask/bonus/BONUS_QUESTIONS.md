## Bonus questions
1. How will you modify the query used in the exercise to support prefix 
search for the last word in the input strings so that the file with 
content "make a long story short" would be matched with "long story sho" 
(edit distance should be still checked as well)?<br>
**ANSWER**:<br>

``` java
Integer maxSlop = 0;

WildcardQuery wildcard = new WildcardQuery(new Term("fileContent", "sho*"));
SpanQuery spanWildcard = new SpanMultiTermQueryWrapper<>(wildcard);

//  "long story sho*"
SpanNearQuery q = new SpanNearQuery(new SpanQuery[] {
        new SpanTermQuery(new Term("fileContent", "long")),
        new SpanTermQuery(new Term("fileContent", "story")),
        spanWildcard},
        maxSlop,
        true);
```

2. How will you modify the query used in the exercise to match files by 
input strings within a given edit distance and the same order of words 
(i.e. word permutations are not allowed)?<br>

