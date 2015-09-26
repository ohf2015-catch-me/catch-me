# catch-me

## API spec

### Game details: GET /game/{gameId}
repose:
```
{
	uuid: 'asdsadsaddsa'
	,text: 'Find me!'
	,picture: 'http://asdsadsa/asdsad.jpg'
	,endTime: '2015-09-26 15:00:00'
	,timeline: [
		{
			type: 'hint'
			,text: 'Ich bin hier!' (optional)
			,picture: 'http://...' (optional)
		}
		, {
			type: 'question'
			,text: 'Are you a girl?'
			,answer: 'y' (y/n/optional)
		}
	]
}
```

### Create game: POST /game

### Ask question: POST /game/{gameId}/question

### Answer question: PUT /game/{gameId}/question/{questionId}

### Give hint: POST /game/{gameId}/hint

### Found: POST /game/{gameId}/found
