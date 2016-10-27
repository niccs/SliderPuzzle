<b>Slider-Puzzle:
===================



Features supported:
-------------

Following features are supported :-
> **Features:**

> - 	4*4 slider puzzle, grid scale is stored in shared preferences, can be customized
> - 	Clicking on a tile is sliding it to an open space, including multiple tiles legal move
> - 	Dragging smoothly move tiles, including multiple tiles legal move.
> - 	On releasing the drag(for a tile and multiple tiles), if move is over halfway to complete, the move is completed and if less than halfway, tiles are reverted to original position.
> - 	 Puzzle is locked for portrait screen. 
> - 	Target SDK is 23 and build tools versions is “23.0.2” and minimum API support is 17(Android 4.2)
> - Once the puzzle is solved “success” toast message is displayed

#### <b class="icon-file"></i> Screen Flow

Screen -1: Image is sliced into small bitmaps and randomly displayed on screen.


![Screen-1](https://cloud.githubusercontent.com/assets/5301598/19755599/c545fbb2-9c62-11e6-9eba-4745ef41ef19.png)

Screen-2 : Once the puzzle is solved, sucess toast is shown

![Screen-2](https://cloud.githubusercontent.com/assets/5301598/19755592/bc172db8-9c62-11e6-8c65-017c3935dbac.png)
#### <b class="icon-folder-open"> Process Flow Diagram:--</b>


![Process flow diagram](https://cloud.githubusercontent.com/assets/5301598/19755761/a3890568-9c63-11e6-89a4-8895ce53afd7.png)




#### <b class="icon-pencil"></i> High Level Process Flow:--

<b>View:--

1.	PuzzleBoardLayout extends RelativeLayout and is inflated dynamically.
2.	Each tile is a imageview, which stores coordinates, the image slice, and image slice handle. This handle is used to calculate the completion of puzzle.
3.	PuzzleBoard (RectF)is created in which the tiles are placed.
4.	Each imageView tile registers ontouchlistener.
5.	This registered event is captured in the PuzzleBoardLayout to check for touch or drag action events.

<b>Processor:--

1.	ImageProcessor:- 
a)	This scales the bitmap to PuzzleBoard dimensions.
b)	Once the image is scaled, it is sliced into n*n pieces.
c)	Each image is placed on the tile randomly.

2.	PuzzleProcessor
a)	This class handles all the processing logic for movement, dragging and then finally swaping the tiles using the TileDataTransferObject(s)

<b>Model:

1.	TileDataTransferObject:-

a)	This object stores all the transfer data information of a tile, the displacement information between various touch points, and the tile rectangle drawing information.
b)	 Once the tiles movement are validated, the required data to be swiped between the two tiles is stored in this.

2.	PuzzleTile:

a)	This store the Tiles coordinate information which is used to draw tile rectangle in layout.
b)	It stores the correct image slice handle, which is used to calculate the success of puzzle completion.

The puzzle grid size is stored in shared preferences, and can be customized by preference screen easily.


