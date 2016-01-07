---
layout: documentation
displayTitle: Count Vectorizer
title: Count Vectorizer
description: Count Vectorizer
usesMathJax: true
includeOperationsMenu: true
---
Extracts a vocabulary from document collections and generates a CountVectorizerModel.

This operation is ported from Spark ML. For more details, see: <a target="_blank" href="http://spark.apache.org/docs/1.5.2/api/scala/index.html#org.apache.spark.ml.feature.CountVectorizer">org.apache.spark.ml.feature.CountVectorizer documentation</a>.

**Since**: Seahorse 1.0.0

## Input

This operation does not take any input.

## Output


<table>
<thead>
<tr>
<th style="width:15%">Port</th>
<th style="width:15%">Type Qualifier</th>
<th style="width:70%">Description</th>
</tr>
</thead>
<tbody>
    <tr><td><code>0</code></td><td><code><a href="../classes/estimator.html">Estimator</a></code></td><td>Estimator that can be used in <a href="fit.html">Fit</a> operation</td></tr>
</tbody>
</table>
    

## Parameters


<table class="table">
<thead>
<tr>
<th style="width:15%">Name</th>
<th style="width:15%">Type</th>
<th style="width:70%">Description</th>
</tr>
</thead>
<tbody>
    
<tr>
<td><code>input column</code></td>
<td><code><a href="../parameters.html#single_column_selector">SingleColumnSelector</a></code></td>
<td>Input column name.</td>
</tr>
    
<tr>
<td><code>min different documents</code></td>
<td><code><a href="../parameters.html#numeric">Numeric</a></code></td>
<td>Specifies the minimum number of different documents a term must appear in to be included in the vocabulary.</td>
</tr>
    
<tr>
<td><code>min term frequency</code></td>
<td><code><a href="../parameters.html#numeric">Numeric</a></code></td>
<td>Filter to ignore rare words in a document. For each document, terms with frequency/count less than the given threshold are ignored. If this is an integer >= 1, then this specifies a count (of times the term must appear in the document); if this is a double in [0,1), then this specifies a fraction (out of the document's token count). Note that the parameter is only used in transform of CountVectorizerModel and does not affect fitting.</td>
</tr>
    
<tr>
<td><code>output column</code></td>
<td><code><a href="../parameters.html#string">String</a></code></td>
<td>Output column name.</td>
</tr>
    
<tr>
<td><code>max vocabulary size</code></td>
<td><code><a href="../parameters.html#numeric">Numeric</a></code></td>
<td>Max size of the vocabulary.</td>
</tr>
    
</tbody>
</table>
    