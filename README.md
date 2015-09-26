# catch-me

## API spec

### Game details: GET /game/{gameId}
repose:
```
{
	uuid: 'asdsadsaddsa'
	,text: 'Find me!'
	,picture: '(base64 encoded)'
	,endTime: '2015-09-26 15:00:00'
	,timeline: [
		{
			type: 'hint'
			,text: 'Ich bin hier!' (optional)
			,picture: '(base64 encoded)' (optional)
		}
		, {
			type: 'question'
			,uuid: '12323'
			,text: 'Are you a girl?'
			,answer: 'y' (y/n/optional)
		}
	]
}
```

### Create game: POST /game
request:
```
{
	text: 'Find me now!'
	,picture: '(base64 encoded)'
}
```

response: (like for GET)

### Get my game: GET /my-game
(404 response if no game by current user)

response:
like Game details, with extra field 'secret'



### Ask question: POST /game/{gameId}/question
request:
```
{
	text: 'Are you a boy?'
}
```

response:
```
{
	uuid: 'sadfdsffds'
	,text: 'Are you a boy?'
}
```

### Answer question: PUT /game/{gameId}/question/{questionId}
request: 
```
{
	answer: 'y'
}
```

response: (like request)

### Give hint: POST /game/{gameId}/hint
request:
```
{
	text: 'I wear a black shirt'
	,picture: '(base64)'
}
```

response (like request)

### Found: POST /game/{gameId}/found
request:
```
{
	secret: 'ASD21334'
}
```

response:

