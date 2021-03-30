# Rule 34 Universe
## What is it?
It is universal android client for rule34 web sites.<br/>
For truth, you can write additional parsers not only to rule34 sites, but also to something like e621.net. 
<br/>For more info click [here](https://github.com/ACLzz/Rule34-Universal-Android-Client/blob/main/README.md#Parser).

## Advantages
- No ads
- Faster loading
- No history
- No trackers
- Open source
- Default tags feature (checkout "tags" section on main menu)

## Disadvantages
- Not supporting videos (GIFs works good)
- Not resizing image
- Not able to download images

## What have I used
- Android fragments
- DataBinding
- FragmentManager
- RecyclerView
- khttp
- jsoup

## Parser
Use parser for rule34.xxx as example.
1) Add your parser to "parsers_array" in values->strings
2) If web source with your content dynamically loading tags suggestions while typing, like on rule34.xxx<br/> => add it's url to "parsers_with_suggestions_loader_array" array in values->strings 
3) Create new class in "parsers" package that extends Parser base class
4) Implement all functions like in rule34xxxParser. Checkout base class for helpful functions
5) Add your class to when expression in parsers.ParserClass.ContentParser

## Paheal parser
As rule34.paheal.net has some restriction to anonymous users, <br/>you can't search more than 3 tags in one search, so your default tags won't work properly.

## Contributing
It is free open source application, you can fork it, sell it, or something.<br/>
But if you want to add your parser, it would be great if you create a merge request and we can build more powerful library.<br/>
Of course I will create a section "Contributors" and write you there :) 
