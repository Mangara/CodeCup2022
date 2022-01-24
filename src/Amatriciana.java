import java.util.List;
import java.io.BufferedReader;
import java.util.Arrays;
import java.io.PrintStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.util.Random;
public class Amatriciana {
public static void main(String[]args)throws IOException{
Player.TIMING=true;
Player.DEBUG=false;
Player.SCORE=true;
Player p=getPlayer();
p.play(new BufferedReader(new InputStreamReader(System.in)),System.out);
}
public static Player getPlayer(){
return new LimitedValueUCTPlayer(new AllMoves(),new EqualTurnTime(29.5),1000,7.0,34.0,new XoRoShiRo128PlusRandom());
}
}
class Move {
private static final int LOCATION_MASK=0b00_111111;
private static final int TILE_MASK    =0b11_000000;
private Move(){
}
public static final int location(final int row,final int column){
return 7*row+column;
}
public static final int row(final int location){
return location/7;
}
public static final int column(final int location){
return location %7;
}
public static final int getLocation(final int move){
return move&LOCATION_MASK;
}
public static final int getRow(final int move){
return row(getLocation(move));
}
public static final int getColumn(final int move){
return column(getLocation(move));
}
public static final int getTile(final int move){
return(move&TILE_MASK)>>6;
}
public static final int setTile(int move,int tile){
return(move&~TILE_MASK)|(tile<<6);
}
public static final int fromLocationTile(final int location,final int tile){
return(tile<<6)|location;
}
public static final int fromLocation(final int location){
return location;
}
public static final int fromRowColumnTile(final int row,final int column,final int tile){
return fromLocationTile(location(row,column),tile);
}
private static final char[]TILE_CHARS=new char[]{'s','l','r'};
public static String toString(int move){
char row=(char)(getRow(move)+'a');
char col=(char)(getColumn(move)+'a');
char tile=TILE_CHARS[getTile(move)];
return new StringBuilder().append(row).append(col).append(tile).toString();
}
public static int fromString(String move){
int row=move.charAt(0)-'a';
int col=move.charAt(1)-'a';
int tile=tileFromChar(move.charAt(2));
return fromRowColumnTile(row,col,tile);
}
private static int tileFromChar(char tile){
switch(tile){
case 's':
return Board.STRAIGHT;
case 'l':
return Board.LEFT;
default:
return Board.RIGHT;
}
}
public static int[]arrayFromList(List<Integer>moves){
int[]result=new int[moves.size()];
int i=0;
for(int move:moves){
result[i]=move;
i++;
}
return result;
}
public static void shuffle(int[]moves,Random rand){
for(int i=moves.length-1;i>0;i--){
int newIndex=rand.nextInt(i+1);
int temp=moves[newIndex];
moves[newIndex]=moves[i];
moves[i]=temp;
}
}
}
abstract class Board {
public static final byte EMPTY=-1;
public static final byte STRAIGHT=0;
public static final byte LEFT=1;
public static final byte RIGHT=2;
private int turn=-2;
private int scoreBlue=50;
private int scoreRed=50;
public abstract byte get(int loc);
public byte get(int row,int col){
return get(Move.location(row,col));
}
public boolean isEmpty(int loc){
return get(loc)==EMPTY;
}
public boolean isEmpty(int row,int col){
return isEmpty(Move.location(row,col));
}
protected abstract int applyRegularMove(int move);
protected abstract int undoRegularMove(int move);
public int getTurn(){
return turn;
}
public int getNumFreeSpaces(){
return 61-turn;
}
public boolean isGameOver(){
return turn==61;
}
public boolean isLegalMove(int move){
return isEmpty(Move.getLocation(move));
}
public void applyMove(int move){
updateScore(applyRegularMove(move));
turn++;
}
public void undoMove(int move){
turn--;
updateScore(undoRegularMove(move));
}
private void updateScore(int scoreDelta){
if(isCurrentPlayerBlue()){
scoreBlue+=scoreDelta;
}else{
scoreRed+=scoreDelta;
}
}
public int getScore(boolean blue){
return blue?scoreBlue:scoreRed;
}
public boolean isCurrentPlayerBlue(){
return turn %2==0;
}
public int scoreAfterMove(int move,boolean blue){
applyMove(move);
int score=getScore(blue);
undoMove(move);
return score;
}
protected void initialize(Board board){
turn=board.getTurn();
scoreBlue=board.getScore(true);
scoreRed=board.getScore(false);
}
protected void initializeTurn(){
int nTiles=0;
for(int loc=0;loc<63;loc++){
if(!isEmpty(loc)){
nTiles++;
}
}
turn=nTiles-2;
}
private static final int LEFT_TILE=Move.fromLocationTile(0,LEFT);
private static final int RIGHT_TILE=Move.fromLocationTile(0,RIGHT);
public int[]possibleMoves(){
int[]moves=new int[3*getNumFreeSpaces()];
int index=0;
for(int loc=0;loc<63;loc++){
if(isEmpty(loc)){
moves[index]=loc;
moves[index+1]=loc|LEFT_TILE;
moves[index+2]=loc|RIGHT_TILE;
index+=3;
}
}
return moves;
}
public int[]emptySpaces(){
int[]moves=new int[getNumFreeSpaces()];
int index=0;
for(int loc=0;loc<63;loc++){
if(isEmpty(loc)){
moves[index]=loc;
index++;
}
}
return moves;
}
public abstract int[]connectingMoves();
public abstract int[]nonNegativeMoves();
public void print(){
System.err.println(" abcdefg");
for(int row=0;row<9;row++){
System.err.print((char)(row+'a'));
for(int col=0;col<7;col++){
System.err.print(charForValue(get(row,col)));
}
System.err.println();
}
System.err.printf("B:%3d R:%3d%n",scoreBlue,scoreRed);
}
private char charForValue(int boardValue){
switch(boardValue){
case EMPTY:
return ' ';
case Board.STRAIGHT:
return 'S';
case Board.LEFT:
return 'L';
case Board.RIGHT:
return 'R';
default:
throw new IllegalArgumentException();
}
}
public void printMoves(){
for(int row=0;row<9;row++){
for(int col=0;col<7;col++){
if(!isEmpty(row,col)){
System.err.println(Move.toString(Move.fromRowColumnTile(row,col,get(row,col))));
}
}
}
}
}
class Paths {
public enum Edge{
FIRST,
SECOND;
}
public enum Side{
TOP,LEFT,BOTTOM,RIGHT;
}
private final int[]endpoint=new int[142];
private final int[]length=new int[142];
public Paths(){
for(int i=0;i<142;i++){
endpoint[i]=i;
}
}
public Paths(Paths paths){
System.arraycopy(paths.endpoint,0,endpoint,0,142);
System.arraycopy(paths.length,0,length,0,142);
}
public Paths(Board board){
this();
for(int loc=0;loc<63;loc++){
if(!board.isEmpty(loc)){
int move=Move.fromLocationTile(loc,board.get(loc));
addEdge(move,Edge.FIRST,true);
addEdge(move,Edge.SECOND,true);
}
}
}
public int[]getPossibleEndpointLocations(int midpoint){
int end=endpoint[midpoint];
if(onBoundary(end)){
return null;
}
return getSquares(end);
}
public boolean anyPathEndsAtRightSide(final int row,final int col){
final int top=15*row+col;
final int left=top+7;
final int right=top+8;
final int bottom=top+15;
return rightBoundary(endpoint[top])
||rightBoundary(endpoint[left])
||rightBoundary(endpoint[right])
||rightBoundary(endpoint[bottom]);
}
public boolean couldBeNegative(final int row,final int col){
final int top=15*row+col;
final int left=top+7;
final int right=top+8;
final int bottom=top+15;
final int endTop=endpoint[top];
final int endLeft=endpoint[left];
final int endRight=endpoint[right];
final int endBottom=endpoint[bottom];
if(endTop==left||endTop==right||endTop==bottom
||endLeft==right||endLeft==bottom
||endRight==bottom){
return true;
}
int nLeft=0;
if(leftBoundary(endTop)){
nLeft++;
}
if(leftBoundary(endLeft)){
nLeft++;
}
if(leftBoundary(endRight)){
nLeft++;
}
if(leftBoundary(endBottom)){
nLeft++;
}
if(nLeft>1){
return true;
}
int nRight=0;
if(rightBoundary(endTop)){
nRight++;
}
if(rightBoundary(endLeft)){
nRight++;
}
if(rightBoundary(endRight)){
nRight++;
}
if(rightBoundary(endBottom)){
nRight++;
}
return nRight>1;
}
private int getMidpoint(final int row,final int col,final Side side){
switch(side){
case TOP:
return 15*row+col;
case LEFT:
return 15*row+col+7;
case BOTTOM:
return 15*row+col+15;
default:
return 15*row+col+8;
}
}
private int[]getSquares(int midpoint){
int row=(midpoint-7)/15;
int withinRow=midpoint-15*row;
if(withinRow<15){
int col=withinRow-8;
return new int[]{
row,col,
row,col+1,};
}else{
int col=withinRow-15;
return new int[]{
row,col,
row+1,col
};
}
}
private boolean leftBoundary(int midpoint){
return midpoint %15==7;
}
private boolean rightBoundary(int midpoint){
return midpoint %15==14;
}
private boolean topBoundary(int midpoint){
return midpoint<7;
}
private boolean bottomBoundary(int midpoint){
return midpoint>134;
}
private boolean onBoundary(int midpoint){
return midpoint<7
||midpoint>134
||midpoint %15==7
||midpoint %15==14;
}
private static final Side[][]FIRST_SIDE_FOR=new Side[][]{
new Side[]{Side.LEFT,Side.BOTTOM},
new Side[]{Side.LEFT,Side.BOTTOM},
new Side[]{Side.LEFT,Side.TOP}
};
private static final Side[][]SECOND_SIDE_FOR=new Side[][]{
new Side[]{Side.RIGHT,Side.TOP},
new Side[]{Side.TOP,Side.RIGHT},
new Side[]{Side.BOTTOM,Side.RIGHT}
};
final public int addEdge(final int move,final Edge edge,final boolean blue){
final int row=Move.getRow(move);
final int col=Move.getColumn(move);
final int tile=Move.getTile(move);
final int mid1=getMidpoint(row,col,FIRST_SIDE_FOR[tile][edge.ordinal()]);
final int mid2=getMidpoint(row,col,SECOND_SIDE_FOR[tile][edge.ordinal()]);
final int end1=endpoint[mid1];
final int end2=endpoint[mid2];
if(end1==mid2){
return-5;
}
final boolean left1=leftBoundary(end1);
final boolean left2=leftBoundary(end2);
final boolean right1=rightBoundary(end1);
final boolean right2=rightBoundary(end2);
int score=0;
if((left1&&left2)||(right1&&right2)){
score=-3;
}else if(left1&&right2){
score=1+(blue?length[end1]:length[end2]);
}else if(right1&&left2){
score=1+(blue?length[end2]:length[end1]);
}
endpoint[end1]=end2;
endpoint[end2]=end1;
final int newLength=length[end1]+length[end2]+1;
length[end1]=newLength;
length[end2]=newLength;
return score;
}
final public int removeEdge(final int move,final Edge edge,final boolean blue){
final int row=Move.getRow(move);
final int col=Move.getColumn(move);
final int tile=Move.getTile(move);
final int mid1=getMidpoint(row,col,FIRST_SIDE_FOR[tile][edge.ordinal()]);
final int mid2=getMidpoint(row,col,SECOND_SIDE_FOR[tile][edge.ordinal()]);
final int end1=endpoint[mid1];
final int end2=endpoint[mid2];
if(length[end1]==1){
endpoint[mid1]=mid1;
endpoint[mid2]=mid2;
length[mid1]=0;
length[mid2]=0;
}else if(end1==end2){
if(length[mid1]>length[mid2]){
endpoint[end1]=mid2;
length[end1]=length[mid2];
endpoint[mid1]=mid1;
length[mid1]=0;
}else{
endpoint[end1]=mid1;
length[end1]=length[mid1];
endpoint[mid2]=mid2;
length[mid2]=0;
}
}else{
endpoint[end1]=mid1;
endpoint[end2]=mid2;
length[end1]=length[mid1];
length[end2]=length[mid2];
}
int score=0;
final int preEnd1=endpoint[mid1];
final int preEnd2=endpoint[mid2];
if(preEnd1==mid2){
score=5;
}else{
final boolean left1=leftBoundary(preEnd1);
final boolean left2=leftBoundary(preEnd2);
final boolean right1=rightBoundary(preEnd1);
final boolean right2=rightBoundary(preEnd2);
if((left1&&left2)||(right1&&right2)){
score=3;
}else if(left1&&right2){
score=-1-(blue?length[preEnd1]:length[preEnd2]);
}else if(right1&&left2){
score=-1-(blue?length[preEnd2]:length[preEnd1]);
}
}
return score;
}
public void checkPaths(int row,int col){
for(Side side:Side.values()){
checkPathFrom(getMidpoint(row,col,side));
}
}
public void printDebug(){
System.err.println("Endpoints:");
System.err.println(Arrays.toString(endpoint));
System.err.println("Lengths:");
System.err.println(Arrays.toString(length));
}
private void checkPathFrom(int midpoint){
int end=endpoint[midpoint];
if(endpoint[end]!=midpoint){
printDebug();
throw new IllegalStateException(
String.format("Incorrect path. Midpoint %d has endpoint %d. Expected midpoint %d to have endpoint %d,but was %d",
midpoint,end,end,midpoint,endpoint[end])
);
}
if(length[end]!=length[midpoint]){
printDebug();
throw new IllegalStateException(
String.format("Length mismatch. Midpoint %d has endpoint %d and length %d,but midpoint %d has length %d",
midpoint,end,length[midpoint],end,length[end])
);
}
if(length[end]<0||length[end]>126){
printDebug();
throw new IllegalStateException(
String.format("Illegal length value. Midpoint %d and its endpoint %d have length %d",
midpoint,end,length[end])
);
}
}
}
class ArrayBoard extends Board {
final byte[]board=new byte[63];
final Paths paths;
public ArrayBoard(){
Arrays.fill(board,EMPTY);
paths=new Paths();
}
public ArrayBoard(byte[][]grid){
for(int i=0;i<9;i++){
System.arraycopy(grid[i],0,this.board,7*i,7);
}
initializeTurn();
paths=new Paths(this);
}
public ArrayBoard(Board board){
if(board instanceof ArrayBoard){
ArrayBoard arrayBoard=(ArrayBoard)board;
System.arraycopy(arrayBoard.board,0,this.board,0,63);
paths=new Paths(arrayBoard.paths);
}else{
for(int loc=0;loc<63;loc++){
this.board[loc]=board.get(loc);
}
paths=new Paths(board);
}
initialize(board);
}
@Override
public byte get(int loc){
return board[loc];
}
@Override
protected int applyRegularMove(int move){
int loc=Move.getLocation(move);
board[loc]=(byte)Move.getTile(move);
return scoreDelta(move,isCurrentPlayerBlue());
}
@Override
protected int undoRegularMove(int move){
int loc=Move.getLocation(move);
board[loc]=EMPTY;
return undoScoreDelta(move,isCurrentPlayerBlue());
}
private int scoreDelta(int move,boolean blue){
return paths.addEdge(move,Paths.Edge.FIRST,blue)+paths.addEdge(move,Paths.Edge.SECOND,blue);
}
private int undoScoreDelta(int move,boolean blue){
return paths.removeEdge(move,Paths.Edge.SECOND,blue)+paths.removeEdge(move,Paths.Edge.FIRST,blue);
}
@Override
public int[]connectingMoves(){
int[]leftSideMidpoints=new int[]{7,22,37,52,67,82,97,112,127};
List<Integer>connectingMoves=new ArrayList<>();
for(int left:leftSideMidpoints){
int[]endSquares=paths.getPossibleEndpointLocations(left);
if(endSquares==null){
continue;
}
int row,col;
if(isEmpty(endSquares[0],endSquares[1])){
row=endSquares[0];
col=endSquares[1];
}else{
row=endSquares[2];
col=endSquares[3];
}
if(paths.anyPathEndsAtRightSide(row,col)){
connectingMoves.add(Move.fromRowColumnTile(row,col,Board.STRAIGHT));
connectingMoves.add(Move.fromRowColumnTile(row,col,Board.LEFT));
connectingMoves.add(Move.fromRowColumnTile(row,col,Board.RIGHT));
}
}
return Move.arrayFromList(connectingMoves);
}
@Override
public int[]nonNegativeMoves(){
int[]moves=new int[3*getNumFreeSpaces()];
int index=0;
for(int row=0;row<9;row++){
for(int col=0;col<7;col++){
if(isEmpty(row,col)&&!paths.couldBeNegative(row,col)){
moves[index]=Move.fromRowColumnTile(row,col,Board.STRAIGHT);
moves[index+1]=Move.fromRowColumnTile(row,col,Board.LEFT);
moves[index+2]=Move.fromRowColumnTile(row,col,Board.RIGHT);
index+=3;
}
}
}
return Arrays.copyOf(moves,index);
}
}
class BitBoard extends Board {
final Paths paths;
long empty=Long.MAX_VALUE;
long straight=0;
long left=0;
long right=0;
public BitBoard(){
paths=new Paths();
}
public BitBoard(byte[][]grid){
for(int row=0;row<9;row++){
for(int col=0;col<7;col++){
set(Move.location(row,col),grid[row][col]);
}
}
initializeTurn();
paths=new Paths(this);
}
public BitBoard(Board board){
if(board instanceof BitBoard){
BitBoard bitBoard=(BitBoard)board;
this.empty=bitBoard.empty;
this.straight=bitBoard.straight;
this.left=bitBoard.left;
this.right=bitBoard.right;
paths=new Paths(bitBoard.paths);
}else{
for(int loc=0;loc<63;loc++){
set(loc,board.get(loc));
}
paths=new Paths(board);
}
initialize(board);
}
@Override
public byte get(final int loc){
final long locBit=1L<<loc;
if((empty&locBit)>0){
return EMPTY;
}
if((straight&locBit)>0){
return STRAIGHT;
}
if((left&locBit)>0){
return LEFT;
}
return RIGHT;
}
@Override
public boolean isEmpty(int loc){
return(empty&(1L<<loc))>0;
}
private String debugString(long state){
StringBuilder sb=new StringBuilder(64);
sb.append(Long.toString(state,2));
while(sb.length()<64){
sb.insert(0,'0');
}
return sb.toString();
}
private void set(final int loc,final int tile){
final long locBit=1L<<loc;
switch(tile){
case EMPTY:
empty|=locBit;
straight&=~locBit;
left&=~locBit;
right&=~locBit;
break;
case STRAIGHT:
empty&=~locBit;
straight|=locBit;
left&=~locBit;
right&=~locBit;
break;
case LEFT:
empty&=~locBit;
straight&=~locBit;
left|=locBit;
right&=~locBit;
break;
default:
empty&=~locBit;
straight&=~locBit;
left&=~locBit;
right|=locBit;
break;
}
}
@Override
protected int applyRegularMove(int move){
int loc=Move.getLocation(move);
set(loc,Move.getTile(move));
return scoreDelta(move,isCurrentPlayerBlue());
}
@Override
protected int undoRegularMove(int move){
int loc=Move.getLocation(move);
set(loc,EMPTY);
return undoScoreDelta(move,isCurrentPlayerBlue());
}
private int scoreDelta(int move,boolean blue){
return paths.addEdge(move,Paths.Edge.FIRST,blue)+paths.addEdge(move,Paths.Edge.SECOND,blue);
}
private int undoScoreDelta(int move,boolean blue){
return paths.removeEdge(move,Paths.Edge.SECOND,blue)+paths.removeEdge(move,Paths.Edge.FIRST,blue);
}
@Override
public int[]emptySpaces(){
final int n=getNumFreeSpaces();
final int[]locations=new int[n];
long tempEmpty=empty;
for(int i=0;i<n;i++){
locations[i]=Long.numberOfTrailingZeros(tempEmpty);
tempEmpty&=~Long.lowestOneBit(tempEmpty);
}
return locations;
}
private static final int LEFT_TILE=Move.fromLocationTile(0,LEFT);
private static final int RIGHT_TILE=Move.fromLocationTile(0,RIGHT);
@Override
public int[]possibleMoves(){
final int n=3*getNumFreeSpaces();
final int[]moves=new int[n];
long tempEmpty=empty;
for(int i=0;i<n;i+=3){
int loc=Long.numberOfTrailingZeros(tempEmpty);
moves[i]=loc;
moves[i+1]=loc|LEFT_TILE;
moves[i+2]=loc|RIGHT_TILE;
tempEmpty&=~Long.lowestOneBit(tempEmpty);
}
return moves;
}
@Override
public int[]connectingMoves(){
int[]leftSideMidpoints=new int[]{7,22,37,52,67,82,97,112,127};
List<Integer>connectingMoves=new ArrayList<>();
for(int left:leftSideMidpoints){
int[]endSquares=paths.getPossibleEndpointLocations(left);
if(endSquares==null){
continue;
}
int row,col;
if(isEmpty(endSquares[0],endSquares[1])){
row=endSquares[0];
col=endSquares[1];
}else{
row=endSquares[2];
col=endSquares[3];
}
if(paths.anyPathEndsAtRightSide(row,col)){
connectingMoves.add(Move.fromRowColumnTile(row,col,Board.STRAIGHT));
connectingMoves.add(Move.fromRowColumnTile(row,col,Board.LEFT));
connectingMoves.add(Move.fromRowColumnTile(row,col,Board.RIGHT));
}
}
return Move.arrayFromList(connectingMoves);
}
@Override
public int[]nonNegativeMoves(){
int[]moves=new int[3*getNumFreeSpaces()];
int index=0;
for(int row=0;row<9;row++){
for(int col=0;col<7;col++){
if(isEmpty(row,col)&&!paths.couldBeNegative(row,col)){
moves[index]=Move.fromRowColumnTile(row,col,Board.STRAIGHT);
moves[index+1]=Move.fromRowColumnTile(row,col,Board.LEFT);
moves[index+2]=Move.fromRowColumnTile(row,col,Board.RIGHT);
index+=3;
}
}
}
return Arrays.copyOf(moves,index);
}
}
class RolloutBoard extends Board {
private final Paths paths;
private long empty=Long.MAX_VALUE;
public RolloutBoard(){
paths=new Paths();
}
public RolloutBoard(byte[][]grid){
for(int row=0;row<9;row++){
for(int col=0;col<7;col++){
set(Move.location(row,col),grid[row][col]);
}
}
initializeTurn();
paths=new Paths(this);
}
public RolloutBoard(Board board){
if(board instanceof RolloutBoard){
RolloutBoard rolloutBoard=(RolloutBoard)board;
this.empty=rolloutBoard.empty;
paths=new Paths(rolloutBoard.paths);
}else if(board instanceof BitBoard){
BitBoard bitBoard=(BitBoard)board;
this.empty=bitBoard.empty;
paths=new Paths(bitBoard.paths);
}else if(board instanceof ArrayBoard){
ArrayBoard arrayBoard=(ArrayBoard)board;
for(int loc=0;loc<63;loc++){
set(loc,board.get(loc));
}
paths=new Paths(arrayBoard.paths);
}else{
for(int loc=0;loc<63;loc++){
set(loc,board.get(loc));
}
paths=new Paths(board);
}
initialize(board);
}
@Override
public byte get(final int loc){
throw new UnsupportedOperationException();
}
@Override
public boolean isEmpty(int loc){
return(empty&(1L<<loc))>0;
}
private void set(final int loc,final int tile){
if(tile!=EMPTY){
empty&=~(1L<<loc);
}
}
@Override
protected int applyRegularMove(int move){
int loc=Move.getLocation(move);
set(loc,Move.getTile(move));
return scoreDelta(move,isCurrentPlayerBlue());
}
@Override
protected int undoRegularMove(int move){
throw new UnsupportedOperationException();
}
private int scoreDelta(int move,boolean blue){
return paths.addEdge(move,Paths.Edge.FIRST,blue)+paths.addEdge(move,Paths.Edge.SECOND,blue);
}
@Override
public int[]emptySpaces(){
final int n=getNumFreeSpaces();
final int[]locations=new int[n];
long tempEmpty=empty;
for(int i=0;i<n;i++){
locations[i]=Long.numberOfTrailingZeros(tempEmpty);
tempEmpty&=~Long.lowestOneBit(tempEmpty);
}
return locations;
}
private static final int LEFT_TILE=Move.fromLocationTile(0,LEFT);
private static final int RIGHT_TILE=Move.fromLocationTile(0,RIGHT);
@Override
public int[]possibleMoves(){
final int n=3*getNumFreeSpaces();
final int[]moves=new int[n];
long tempEmpty=empty;
for(int i=0;i<n;i+=3){
int loc=Long.numberOfTrailingZeros(tempEmpty);
moves[i]=loc;
moves[i+1]=loc|LEFT_TILE;
moves[i+2]=loc|RIGHT_TILE;
tempEmpty&=~Long.lowestOneBit(tempEmpty);
}
return moves;
}
@Override
public int[]connectingMoves(){
throw new UnsupportedOperationException();
}
@Override
public int[]nonNegativeMoves(){
throw new UnsupportedOperationException();
}
}
class XoRoShiRo128PlusRandom extends Random {
private static final long serialVersionUID=1L;
private long s0,s1;
protected XoRoShiRo128PlusRandom(final long s0,final long s1){
this.s0=s0;
this.s1=s1;
}
public XoRoShiRo128PlusRandom(final long seed){
setSeed(seed);
}
public XoRoShiRo128PlusRandom(){
this(randomSeed());
}
private static final XoRoShiRo128PlusRandom seedUniquifier=new XoRoShiRo128PlusRandom(System.nanoTime());
private static long randomSeed(){
final long x;
synchronized(seedUniquifier){
x=seedUniquifier.nextLong();
}
return x^System.nanoTime();
}
public XoRoShiRo128PlusRandom copy(){
return new XoRoShiRo128PlusRandom(s0,s1);
}
@Override
public long nextLong(){
final long s0=this.s0;
long s1=this.s1;
final long result=s0+s1;
s1^=s0;
this.s0=Long.rotateLeft(s0,24)^s1^s1<<16;
this.s1=Long.rotateLeft(s1,37);
return result;
}
@Override
public int nextInt(){
return(int)(nextLong()>>>32);
}
@Override
public int nextInt(final int n){
return(int)nextLong(n);
}
public long nextLong(final long n){
if(n<=0){
throw new IllegalArgumentException("illegal bound "+n+"(must be positive)");
}
long t=nextLong();
final long nMinus1=n-1;
if((n&nMinus1)==0){
return(t>>>Long.numberOfLeadingZeros(nMinus1))&nMinus1;
}
for(long u=t>>>1;u+nMinus1-(t=u %n)<0;u=nextLong()>>>1);
return t;
}
@Override
public double nextDouble(){
return(nextLong()>>>11)*0x1.0p-53;
}
public double nextDoubleFast(){
return Double.longBitsToDouble(0x3FFL<<52|nextLong()>>>12)-1.0;
}
@Override
public float nextFloat(){
return(nextLong()>>>40)*0x1.0p-24f;
}
@Override
public boolean nextBoolean(){
return nextLong()<0;
}
@Override
public void nextBytes(final byte[]bytes){
int i=bytes.length,n=0;
while(i!=0){
n=Math.min(i,8);
for(long bits=nextLong();n--!=0;bits>>=8){
bytes[--i]=(byte)bits;
}
}
}
protected XoRoShiRo128PlusRandom jump(final long[]jump){
long s0=0;
long s1=0;
for(final long element:jump){
for(int b=0;b<64;b++){
if((element&1L<<b)!=0){
s0^=this.s0;
s1^=this.s1;
}
nextLong();
}
}
this.s0=s0;
this.s1=s1;
return this;
}
private static final long[]JUMP={0xdf900294d8f554a5L,0x170865df4b3201fcL};
public XoRoShiRo128PlusRandom jump(){
return jump(JUMP);
}
private static final long[]LONG_JUMP={0xd2a98b26625eee7bL,0xdddf9b1090aa7ac1L};
public XoRoShiRo128PlusRandom longJump(){
return jump(LONG_JUMP);
}
public XoRoShiRo128PlusRandom split(){
nextLong();
final XoRoShiRo128PlusRandom split=copy();
long h0=s0;
long h1=s1;
long h2=s0+0x55a650a4c1dac3e9L;
long h3=s1+0xb39ae98dfa439b73L;
h2=Long.rotateLeft(h2,50);
h2+=h3;
h0^=h2;
h3=Long.rotateLeft(h3,52);
h3+=h0;
h1^=h3;
h0=Long.rotateLeft(h0,30);
h0+=h1;
h2^=h0;
h1=Long.rotateLeft(h1,41);
h1+=h2;
h3^=h1;
h2=Long.rotateLeft(h2,54);
h2+=h3;
h0^=h2;
h3=Long.rotateLeft(h3,48);
h3+=h0;
h1^=h3;
h0=Long.rotateLeft(h0,38);
h0+=h1;
h2^=h0;
h1=Long.rotateLeft(h1,37);
h1+=h2;
h3^=h1;
h2=Long.rotateLeft(h2,62);
h2+=h3;
h0^=h2;
h3=Long.rotateLeft(h3,34);
h3+=h0;
h1^=h3;
h0=Long.rotateLeft(h0,5);
h0+=h1;
h2^=h0;
h1=Long.rotateLeft(h1,36);
h1+=h2;
split.s0=h0;
split.s1=h1;
return split;
}
@Override
public void setSeed(final long seed){
final Random r=new Random(seed);
s0=r.nextLong();
s1=r.nextLong();
}
public void setState(final long[]state){
if(state.length!=2){
throw new IllegalArgumentException("The argument array contains "+state.length+" longs instead of "+2);
}
s0=state[0];
s1=state[1];
}
}
interface StopCriterion {
public abstract void started();
public abstract boolean shouldStop();
public abstract String name();
}
class IterationCount implements StopCriterion {
private final int maxIterations;
public IterationCount(int maxIterations){
this.maxIterations=maxIterations;
}
public int getMaxIterations(){
return maxIterations;
}
private int iteration=0;
@Override
public void started(){
iteration=0;
}
@Override
public boolean shouldStop(){
return iteration++>maxIterations;
}
@Override
public String name(){
return "I"+maxIterations;
}
}
class EqualTurnTime implements StopCriterion {
private final double totalSeconds;
private final long nsPerTurn;
public EqualTurnTime(double totalSeconds){
this.totalSeconds=totalSeconds;
double secondsPerTurn=totalSeconds/30;
this.nsPerTurn=(long)(secondsPerTurn*1_000_000_000);
}
private long started;
@Override
public void started(){
started=System.nanoTime();
}
@Override
public boolean shouldStop(){
return System.nanoTime()-started>nsPerTurn;
}
@Override
public String name(){
return String.format("EqT%.1f",totalSeconds);
}
}
abstract class Player {
public static boolean TIMING=false;
public static boolean DEBUG=false;
public static boolean SCORE=false;
protected final String name;
protected Board board;
protected boolean blue;
public Player(String name){
this.name=name;
}
public String getName(){
return name;
}
public boolean isBlue(){
return blue;
}
public Board getBoard(){
return board;
}
public void play(BufferedReader in,PrintStream out)throws IOException{
long start=0;
if(TIMING){
start=System.currentTimeMillis();
}
initialize(false);
if(TIMING){
System.err.printf("TI:%dms%n",System.currentTimeMillis()-start);
}
playOpening(in,out);
for(String input=in.readLine();!(input==null||"Quit".equals(input));input=in.readLine()){
if(TIMING){
start=System.currentTimeMillis();
}
processMove(Move.fromString(input));
if(SCORE){
System.err.printf("%s-B:%dR:%d%n",input,board.getScore(true),board.getScore(false));
}
int move=move();
if(TIMING){
System.err.printf("T%d:%dms%n",board.getTurn()-1,System.currentTimeMillis()-start);
}
if(SCORE){
System.err.printf("%s-B:%dR:%d%n",Move.toString(move),board.getScore(true),board.getScore(false));
}
out.println(Move.toString(move));
}
}
private void playOpening(BufferedReader in,PrintStream out)throws IOException{
long start=0;
if(TIMING){
start=System.currentTimeMillis();
}
processMove(Move.fromString(in.readLine()));
processMove(Move.fromString(in.readLine()));
if(TIMING){
System.err.printf("Processing jury moves took %d ms.%n",System.currentTimeMillis()-start);
start=System.currentTimeMillis();
}
String input=in.readLine();
if("Start".equals(input)){
blue=true;
}else{
blue=false;
processMove(Move.fromString(input));
}
int move=move();
if(TIMING){
System.err.printf("First move took %d ms.%n",System.currentTimeMillis()-start);
}
out.println(Move.toString(move));
}
public void initialize(boolean blue){
initialize(new ArrayBoard(),blue);
}
public void initialize(Board board,boolean blue){
this.board=board;
this.blue=blue;
}
public void processMove(int move){
board.applyMove(move);
}
public int move(){
int move=selectMove();
processMove(move);
return move;
}
protected abstract int selectMove();
}
class LimitedValueUCTPlayer extends Player {
private static final int MAX_ITERATIONS=200_000;
private final MoveGenerator generator;
private final StopCriterion stop;
private final int ucbLimit;
private final double explorationWeight;
private final double valueWeight;
private final Random rand;
public LimitedValueUCTPlayer(MoveGenerator generator,StopCriterion stop,int ucbLimit,double explorationWeight,double valueWeight,Random rand){
super(String.format("LimValUCT-%s-%s-%d-%.2f-%.2f",generator.name(),stop.name(),ucbLimit,explorationWeight,valueWeight));
this.generator=generator;
this.stop=stop;
this.ucbLimit=ucbLimit;
this.explorationWeight=explorationWeight;
this.valueWeight=valueWeight;
this.rand=rand;
}
private double[]sqrt=null;
private double[]sqrtLog=null;
@Override
protected int selectMove(){
stop.started();
if(sqrt==null){
precomputeSquareRoots();
}
TreeNode root=new TreeNode(null,-1,0,board,isBlue());
int majority=(stop instanceof IterationCount?((IterationCount)stop).getMaxIterations()/2:MAX_ITERATIONS/2);
int maxExpandedTurn=board.getTurn();
for(int i=0;i<MAX_ITERATIONS&&!stop.shouldStop();i++){
TreeNode node=root;
TreeNode firstMoveNode=null;
Board b=new RolloutBoard(board);
while(node.isFullyExpanded()){
node=node.selectMoveNode();
b.applyMove(node.move);
if(firstMoveNode==null){
firstMoveNode=node;
}
}
if(!node.isLeaf()){
node=node.expand(b);
}
maxExpandedTurn=Math.max(maxExpandedTurn,b.getTurn());
int[]score=rollout(b);
while(node!=null){
node.updateValue(score);
node=node.parent;
}
if(firstMoveNode!=null&&firstMoveNode.visits>=majority){
break;
}
}
TreeNode result=root.mostVisitedChild();
if(Player.SCORE){
System.err.printf("%s(%.2f,%d)D=%d%n",Move.toString(result.move),result.value,result.visits,maxExpandedTurn);
}
return result.move;
}
private int[]rollout(Board b){
int[]empty=b.emptySpaces();
Move.shuffle(empty,rand);
for(int i=0;i<empty.length;i++){
b.applyMove(Move.setTile(empty[i],rand.nextInt(3)));
}
return new int[]{b.getScore(true),b.getScore(false)};
}
private void precomputeSquareRoots(){
sqrt=new double[MAX_ITERATIONS+1];
sqrtLog=new double[MAX_ITERATIONS+1];
for(int i=0;i<=MAX_ITERATIONS;i++){
sqrt[i]=Math.sqrt(i);
sqrtLog[i]=Math.sqrt(explorationWeight*Math.log(i));
}
}
private class TreeNode{
final TreeNode parent;
final int move;
final int moveScoreDelta;
final boolean blueToMove;
final int[]possibleMoves;
final List<TreeNode>children=new ArrayList<>();
int unexpandedMoveCount;
int visits=0;
double value=0;
private TreeNode(final TreeNode parent,final int move,final int moveScoreDelta,final Board b,final boolean blueToMove){
this.parent=parent;
this.move=move;
this.moveScoreDelta=moveScoreDelta;
this.blueToMove=blueToMove;
this.possibleMoves=generator.generateMoves(b);
this.unexpandedMoveCount=possibleMoves.length;
}
private boolean isLeaf(){
return possibleMoves.length==0;
}
private boolean isFullyExpanded(){
return unexpandedMoveCount==0&&possibleMoves.length>0;
}
private TreeNode expand(final Board b){
final int moveIndex=rand.nextInt(unexpandedMoveCount);
final int childMove=possibleMoves[moveIndex];
unexpandedMoveCount--;
possibleMoves[moveIndex]=possibleMoves[unexpandedMoveCount];
int scoreBefore=b.getScore(blueToMove);
b.applyMove(childMove);
int scoreDelta=b.getScore(blueToMove)-scoreBefore;
final TreeNode child=new TreeNode(this,childMove,scoreDelta,b,!blueToMove);
children.add(child);
return child;
}
private TreeNode selectMoveNode(){
if(visits<ucbLimit){
return randomChild();
}else{
return bestUCBChild();
}
}
private TreeNode randomChild(){
return children.get(rand.nextInt(children.size()));
}
private TreeNode bestUCBChild(){
TreeNode best=null;
double bestValue=Double.NEGATIVE_INFINITY;
final double parentVisitFactor=sqrtLog[visits];
for(final TreeNode child:children){
final double ucbValue=child.isLeaf()?child.value:child.value+(valueWeight*child.moveScoreDelta+parentVisitFactor)/sqrt[child.visits];
if(ucbValue>bestValue){
bestValue=ucbValue;
best=child;
}
}
return best;
}
private TreeNode mostVisitedChild(){
TreeNode mostVisited=null;
int mostVisits=-1;
for(final TreeNode child:children){
if(child.visits>mostVisits){
mostVisited=child;
mostVisits=child.visits;
}
}
return mostVisited;
}
private void updateValue(final int score[]){
final int nodeScore=blueToMove?score[1]:score[0];
value=value*visits+nodeScore;
visits++;
value/=visits;
}
}
}
abstract class MoveGenerator {
public abstract int[]generateMoves(Board board);
public abstract String name();
public void initialize(Board board){
}
public void applyMove(Board board,int move){
}
public void undoMove(Board board,int move){
}
}
class AllMoves extends MoveGenerator {
@Override
public String name(){
return "All";
}
@Override
public int[]generateMoves(Board board){
return board.possibleMoves();
}
}
