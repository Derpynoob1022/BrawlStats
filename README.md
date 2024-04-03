# My Personal Project

## Description

**What will the application do?**

&nbsp;&nbsp;&nbsp;&nbsp;I want to make an application to track my
stats for a game I am playing right now called Brawl Stars. I've always
wanted to see what my win rate is and my k/d (kill death ratio) in the game
and whether I've improved or not over time.

Things I will add:
- Kill death ratio
- Playtime (estimate)
- Individual character statistics
- Overall trophy gain or over a particular time interval 
- Average damage per match
- Coin gain from drops (potentially)

**Who will use it?**

&nbsp;&nbsp;&nbsp;&nbsp;I would honestly use this to track my gameplay but
I know that my friends would also be interested in this application.

**Why is this project of interest to you?**

&nbsp;&nbsp;&nbsp;&nbsp;I just wanted to make something that is actually useful for me
to use daily. Monitoring the games I've participated in could not only provide insights 
into my gameplay tactics but also facilitate my improvement as a player.
I also think I would enjoy making something like this because then it won't 
feel like school work but rather a hobby.

## User Stories
**Phase 0**
- As a user, I want to be able to add an entry to the match history.
- As a user, I want to be able to view the list of scores I've gotten in previous games.
- As a user, I want to be able to view my kill/death ratio.
- As a user, I want to be able to see particular character statistics.

**Phase 2**
- As a user, I want to be able to save my list of matches I've added
- As a user, I want to be able to be able to load my saved logs


**Phase 4: Task 2**
Mon Apr 01 20:03:33 PDT 2024
[Character: Jerry | Kills: 1 | Deaths: 2 | K/D ratio: 0.50 | Damage: 3 | Star player: false | Trophy gain: +4] has been removed from MatchList

Mon Apr 01 20:03:41 PDT 2024
[Character: 321 | Kills: 41 | Deaths: 15 | K/D ratio: 2.73 | Damage: 15 | Star player: false | Trophy gain: +231312321] has been added to the MatchList

Mon Apr 01 20:03:51 PDT 2024
Changed from
[Character: 321 | Kills: 41 | Deaths: 15 | K/D ratio: 2.73 | Damage: 15 | Star player: false | Trophy gain: +231312321] to
[Character: Jerry | Kills: 41 | Deaths: 15 | K/D ratio: 2.73 | Damage: 15 | Star player: false | Trophy gain: +231312321]

**Phase 4: Task 3**
Some refactoring I could've done is that I feel like my MatchList has low cohesion as it performs multiple
different operations. According to the Single Responsibility Principle, this class shouldn't do too much. So one thing
I could've done is that I could make a new class called Calculator where it calculates the statistics of list of the matches 
because the code for calculation currently sits in the MatchList class. This wouldn't necessary make the program 
run faster but this would make the code easier to understand and easier for me to add new functionality to this project. 

Otherwise, there isn't much else I could find as the coupling of the classes is low and there isn't any design patterns that
I can implement. 





