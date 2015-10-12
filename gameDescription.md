# CATCH ME

## QUESTIONS/ TO DOs:

  - Create/ discuss point system
  - Implement setting and displaying of time left
  - Modify start view
  - TBD: Add to all the views the title and hamburger menu with entries 'Profile' and 'Settings'
  - to finilize end-of-game procedure:
    * found
    * time extention
    * manually finished game
  - Q: should be game finished after the first success or could it allow to win more than one scout. If the last point is true, what happens with points for a user who owns a game?
  

## VIEWS

  1. Start-View:
    * 2 Buttons - 'Create game' and 'Choose game';
  2. 'Create-Game'-View:
    * accessable from the start view via 'create game' button;
    * asks for an initial hint and a picture (displays chosen/made picture);
    * set-end-time field (I would use predefined time intervalls 15, 30 , 45 ...minutes);
    * buttons 'cancel' and 'start game';
  3. 'List-of-games'-View:
    * accessable from the start-view; displays list of the existing games,
in case of 'create game' option was chosen with the own game right on the top of the list.
    * to display: picture and initial hint + time left;
  3. 'Own-Game'-View:
    * list of the hints and questions(colored depending on answer: yes, no or not answered yet);
    * buttons to answer: yes, no and ignore (TBD);
    * time left;
    * buttons 'extend time', 'end game';
    * points pool depending on the point system);
  4. 'Scout-role'-View
    * List of the given hints and questions colored depending on the given answer 'yes' or 'no';
    * Questions of other users without answer are not to display;
    * Questions asked by user without answer are to display as pending (if question was ignored or cancelled user has to be notified somehow :) )(TBD);
    * text input line and button 'Send';
    * time left;
    * points pool depending on the point system);
  5. 'User-Profile'-View: 
    * accessable from all views from the hamburger menu (to be added?);
    * displays user points;
  6. Settings-View:
    * TBD

## GAME DESCRIPTION

### Points system

#### Actions '+' points:
      - Find somebody;
      - With 'yes' answered question (only for scouts);
      
#### Actions '-' points:
      - with 'no' answered questions (only for scouts); 
