# CodeCup2022

5th place AI for the [2022 CodeCup challenge](https://www.codecup.nl/competition.php?comp=280), playing the game of [Spaghetti](https://www.codecup.nl/spaghetti/rules.php).

## Strategy

Amatriciana is a fairly standard [UCT Monte Carlo](https://en.wikipedia.org/wiki/Monte_Carlo_tree_search) player.



## Data structures




## Navigating the code

The final submission is [Amatriciana](src/Amatriciana.java), but it is not very readable. As the CodeCup has a file size limit, I minify the submissions (using [SubmissionPackager](src/codecup2022/tools/SubmissionPackager.java)) so I don't have to worry about whitespace and comments during development.

The entrypoint for the bot is [PlayerRunner](src/codecup2022/runner/PlayerRunner.java), which creates and starts the player. For the final competition, that was a [LimitedValueUCTPlayer](src/codecup2022/player/LimitedValueUCTPlayer.java). This class contains the Monte Carlo Tree search logic.

The important data structures are in the `data` package, and include [Move](src/codecup2022/data/Move.java), [Paths](src/codecup2022/data/Paths.java), and the various implementations of the [Board](src/codecup2022/data/Board.java) class.

The `movegenerator` package contains various classes that generate interesting moves from given a board position, although my final submission only uses the [AllMoves](src/codecup2022/movegenerator/AllMoves.java) generator. The `player` package contains many different players, which were either building blocks, experiments, or opponents in local test competitions. The local test competitions were run with the classes in the `runner` and `tools` packages. Finally, the `stopcriterion` package contains two simple ways of deciding when a player should stop looking for a better move.
