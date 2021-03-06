from django.views.decorators.csrf import csrf_exempt
from django.shortcuts import render, get_object_or_404
from django.shortcuts import render_to_response
from django.http import HttpResponse
from django.template import loader
from datetime import datetime
from collections import Counter
from time import sleep
import json,requests,os, unicodedata, re, operator
import tweepy

# Create your views here.
class TwitterStats:
    @csrf_exempt
    def index(request):
        template = loader.get_template('index.html')
        return HttpResponse(template.render())

    @staticmethod
    def getTwitterApi():
        """
	    This method returns ready use Twitter API object.

		author: Enes Cakir
        """
        consumerKey = 'PjHfYvauYeQ7INPtpJuVJ9I1n'
        consumerSecret = 'lSmSdzC5ikfxp1ngY88oXLjlsqlTPuzQCVziACG4RMKvlcaW20'
        accessToken = '861900081443766274-InQx7aZOUAPF7BEZ8JuexYsFbnGzxct'
        accessTokenSecret = 'muXAKPwSlfLl5px9h990KXIKw6C40UjcLDHhRSLapI92Y'

        auth = tweepy.OAuthHandler(consumerKey, consumerSecret)
        auth.set_access_token(accessToken, accessTokenSecret)

        return tweepy.API(auth)

    # Example for Twitter usage
    def example(request):
        """
	    This method shows example usage of Twitter API.

		author: Enes Cakir
        """
        # Get Twitter API
        api = TwitterStats.getTwitterApi()

        # Get tweets from timeline
        # You can check other methods from: http://tweepy.readthedocs.io/en/v3.5.0/getting_started.html
        public_tweets = api.home_timeline()

        # Print tweets to console
        for tweet in public_tweets:
            print(tweet.text + '\n')

        # Returns response of twitter. It's looks like messy. Don't be coward :)
        return HttpResponse(public_tweets)

    @csrf_exempt
    def postTweet(request):
        """
	    This method post a tweet.
	    It has two paramters, username and tweet where both of them are compulsory.
	    If the username or the tweet is not provided, method returns an error.

		author: Abdullatif Köksal
        """
        api = TwitterStats.getTwitterApi()
        tweet = ""
        test_user = ""
        if request.GET.get('username'):
            # User name of the user to look for
            test_user = request.GET.get('username')
        else:
            return HttpResponse("No username!")
        if request.GET.get('tweet'):
            # Text of the tweet you want to send
            tweet = request.GET.get('tweet')
        else:
            return HttpResponse("No tweet to post!")
        api.update_status(status = tweet)
        return HttpResponse(tweet)

    def getUsersTweetingMostFrequently(request):
        """
        This method finds users who has been tweeting most frequently (on hourly basis).
        It checks for the time difference between request and tweets.
        The tweets are collected from timeline.

        author: Anil Seyrek
        """
        api = TwitterStats.getTwitterApi()
        users = []
        usersSorted = []
        public_tweets = api.home_timeline()

        for tweet in public_tweets:
            # Take time difference for 1 hour difference
            diff = datetime.now() - tweet.created_at
            if diff.total_seconds() < 3600:
                # Add most frequently tweeting users to the list
                users.append(tweet.user.id)

        # Sort users
        for count, elem in sorted(((users.count(e), e) for e in set(users)), reverse=True):
            usersSorted.append(api.get_user(elem))

        #Return sorted list
        return HttpResponse(usersSorted)


    def getFrequencyOfWordsOfLikedTweets(request):
        """
	    This method counts the frequency of the words a specific user liked.
	    It has two paramters, username and count where username is compulsory.
	    If a username is not provided, method returns a string that expresses this fact.
	    If count is not provided, it is defaulted to 100, hence only 100 tweets is searched.

		author: Riza Ozcelik
        """
        api = TwitterStats.getTwitterApi()
        count = 100
        test_user = ""
        if request.GET.get('username'):
            # User name of the user to look for
            test_user = request.GET.get('username')
        else:
            return HttpResponse("NO USERNAME!")
        if request.GET.get('count'):
            count = int(request.GET.get('count'))
        #find each favorited tweet
        tweets = []
        pages = tweepy.Cursor(api.favorites,id=test_user,wait_on_rate_limit=True, count=200).pages(200)
        brokenInnerLoop = False
        for page in pages:
            for status in page:
                tweets.append(str(status.text))
                # break  when enough tweet is collected.
                if len(tweets) >= count:
                    brokenInnerLoop = True
                    break
            if brokenInnerLoop:
                break
        #find all words anc count them
        allWords = [s  for t in tweets for s in t.split(' ')]
        counts = {w: allWords.count(w) for w in allWords}

        #ignore usual suspects
        if '' in counts:
            del counts['']
        if ' ' in counts:
            del counts[' ']

        d = {"frequencies":[{'word':key,"frequency":value} for key,value in counts.items()], "count":len(tweets)}
        json_string = json.dumps(d)
        return HttpResponse(json_string)

    def getMostNumberOfFollowers(request):
        """
        This method returns the user who has the most number of followers in the list of the specific user's(given as parameter) followers.
        If username is not given as a parameter, it returns No Username
        If count parameter isn't give, it counts 100 follower as a default.

        author: Barın Özmen
        """
        api = TwitterStats.getTwitterApi()
        count = 100
        test_user = ""
        if request.GET.get('username'):
            test_user = request.GET.get('username')
        else:
            return HttpResponse("NO USERNAME!")
        if request.GET.get('count'):
            count = request.GET.get('count')

        maxFollower = 0
        name = ""

        for follower in tweepy.Cursor(api.followers, id=test_user, wait_on_rate_limit=True, count=count).items():
            if(maxFollower<follower.followers_count):
                name = follower.screen_name
                maxFollower = follower.followers_count

        return HttpResponse(name)

    def getMostLikedPages(request):
        """
        This method returns three most liked pages of the user, based on likes.

        author: Giray Eryilmaz
        """
        api = TwitterStats.getTwitterApi()
        count = 100
        test_user = ""
        if request.GET.get('username'):
            # User name of the user to look for
            test_user = request.GET.get('username')
        else:
            return HttpResponse("NO USERNAME!")
        if request.GET.get('count'):
            count = request.GET.get('count')
        #find each favorited tweet
        userNames = []
        ids = []
        pages = tweepy.Cursor(api.favorites,id=test_user,wait_on_rate_limit=True, count=count).pages(200)
        for page in pages:
            for status in page:
                userNames.append(status.user.name)
                ids.append(status.user.id)


        topIDs = []
        data = Counter(ids)
        try:
            topIDs.append(data.most_common(3)[0][0])
            topIDs.append(data.most_common(3)[1][0])
            topIDs.append(data.most_common(3)[2][0])
        except:
            pass

        topThree = []
        data = Counter(userNames)
        try:
            topThree.append(data.most_common(3)[0][0])
            topThree.append(data.most_common(3)[1][0])
            topThree.append(data.most_common(3)[2][0])
        except:
            pass


        try:
            tuples = []
            tuples.append((topIDs[0],topThree[0]))
            tuples.append((topIDs[1],topThree[1]))
            tuples.append((topIDs[2],topThree[2]))
        except:
            pass
        #print(tuples)

        return HttpResponse(tuples)

    def getWhoMentionedMost(request):
        """
        This method to find the follower who has recently been mentioned most by the authenticated user the most.

        author: Ezgi Yuceturk
        """
        # Get twitter api with authenticated user
        api = TwitterStats.getTwitterApi()
        test_user=""
        usrName = ""
        if request.GET.get('username'):
            test_user = request.GET.get('username')
        else:
            return HttpResponse("NO USERNAME!")
            mention_map = {}
        # Get the user information f authenticated user. Returns user object.
        user = api.get_user(test_user)
        sleep(0.1)
        # Get followers of the authenticated user. Returns list of user objects
        friendList =  api.followers(test_user)
        # Get the recent 100 tweets of authenticated user. Returns list of status objects.
        f_statusList = api.user_timeline(user.id,count=100)
        # For each foolowers this loops checks if their screen name appears in a tweet and if so, counts
        # how mant times it appears in 100 tweets.
        for friend in friendList:
            f_screenName = friend.screen_name
            pattern = re.compile("@"+f_screenName+"\s")
            mention_map[f_screenName] = 0
            for tweets in f_statusList:
                t_text = tweets.text
                if(pattern.match(t_text)):
                    mention_map[f_screenName] = mention_map[f_screenName]+1
        # Calculated the biggest value in map of screen name and appearance count
        maxMention = max(mention_map, key = lambda i:mention_map[i])
        # Return the screen name of max value.
        return HttpResponse(maxMention)

    def getFrequencyOfWordsOfAllTweets(request):
        """
	    This method counts the frequency of the words a specific user own tweets.
	    It has two paramters, username and count where username is compulsory.
	    If a username is not provided, method returns a string that expresses this fact.
	    If count is not provided, it is defaulted to 1, hence only 1 tweets is searched.

		author: Fatih Asagidag
        """
        api = TwitterStats.getTwitterApi()
        cnt = 1
        test_user = ""
        tweets = []

        # Validation of username
        if request.GET.get('username'):
            # User name of the user to look for
            test_user = request.GET.get('username')
        else:
            return HttpResponse("NO USERNAME!")

        # Check if user enter count number, otherwise use default one
        if request.GET.get('count'):
            cnt = request.GET.get('count')

        #find each user's tweets
        #Note that, status object has some contents to distinguish user's own tweets from others in user timeline.
        #For example, status.entities.user_mentions.screen_name gives name of owner of tweet but it could be sometimes NULL
        # if user didn't fill his profile information. Also, there is "retweeted" part of status which indicates if tweet is
        # retweeted or not but it is not useful for our condition, either. Finally, I choose to compare first three character
        #of text which are always 'RT ' if it is retweeted.
        for status in tweepy.Cursor(api.user_timeline,id=test_user,wait_on_rate_limit=True).items(int(cnt)):
            first_three_letters = status.text[:3]
            if first_three_letters != 'RT ':
                tweets.append(str(status.text))
                print(status.text)

        #find all words anc count them
        allWords = [s  for t in tweets for s in t.split(' ')]
        counts = {w: allWords.count(w) for w in allWords}

        #ignore usual suspects
        if '' in counts:
            del counts['']
        if ' ' in counts:
            del counts[' ']

        d = {"frequencies":[{'word':key,"frequency":value} for key,value in counts.items()]}
        json_string = json.dumps(d)
        return HttpResponse(json_string)

    def getLikeRatioOfTwoUsers(request):
        """
        This method takes two usernames and finds the like counts of each others' posts.
        Returns 'MISSING USERNAME' when at least one username is not provided.
        100 tweets are taken from timeline for both users.

        author: Hilal Benzer
        """
        api = TwitterStats.getTwitterApi()
        user1 = ""
        user2 = ""
        count = 100
        if request.GET.get('user1'):
            user1 = request.GET.get('user1')
        else:
            return HttpResponse("MISSING USERNAME!")
        if request.GET.get('user2'):
            user2 = request.GET.get('user2')
        else:
            return HttpResponse("MISSING USERNAME!")
        pages1 = tweepy.Cursor(api.favorites, id=user1, wait_on_rate_limit=True, count=count).pages(200)
        count1 = 0
        for page in pages1:
            for status in page:
                if status.user.screen_name == user2:
                    count1 += 1
        pages2 = tweepy.Cursor(api.favorites, id=user2, wait_on_rate_limit=True, count=count).pages(200)
        count2 = 0
        for page in pages2:
            for status in page:
                if status.user.screen_name == user1:
                    count2 += 1
        d = {'user1': user1, 'count1': count1, 'user2': user2, 'count2': count2}
        json_string = json.dumps(d)
        return HttpResponse(json_string)

    def hashtagPercentage(request):
        """
        This method takes username of a twitter account and returns how frequently the user
        uses hastags in their tweets. Basically, it counts all tweets and tweets with # character and
        then returns the percentage of them.

        author: Halil Kalkan
        """
        api = TwitterStats.getTwitterApi()
        count = 100
        test_user = ""
        tweets = []
        count1 = 0
        count2 = 0
        if request.GET.get('username'):
            # User name of the user to look for
            test_user = request.GET.get('username')
        else:
            return HttpResponse("NO USERNAME!")

        pages = tweepy.Cursor(api.user_timeline,id=test_user,wait_on_rate_limit=True, count = count).pages(200)
        #Pattern for words with hashtag
        pattern = re.compile("#\w*")
        #For each page it counts tweets, also counts tweets which contains hashtag sign
        for pg in pages:
            for status in pg:
                count1 += 1
                t_text = status.text
                if(pattern.match(t_text)):
                    count2 += 1
                else:
                    count2 += 0

        return HttpResponse(count2/count1)
