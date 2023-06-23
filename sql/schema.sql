SELECT lastname, timesplayed
FROM leaderboard;

SELECT DISTINCT hand, rank
FROM pokerhands;

SELECT first_name,last_name
FROM theplayers;

SELECT l.lastname
FROM leaderboard l
INNER JOIN theplayers p ON l.lastname = p.last_name
WHERE p.hand ='specific_hand';

SELECT p.first_name, p.last_name, p.hand
FROM  theplayers p
INNER JOIN leaderboard l ON p.last_name = l.lastname
WHERE p.hand = 'specific_hand';