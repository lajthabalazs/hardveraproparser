#!/usr/bin/env python
# -*- coding: UTF-8 -*-
from django.shortcuts import render, get_object_or_404
import logging
import datetime
import urllib2
import re

from django.http.response import Http404

logger = logging.getLogger(__name__)

def index(request):
    userUrl = request.POST.get("userUrl", "http://prohardver.hu/tag/mogyesz75.html")
    # TODO extract user name from URL
    response = urllib2.urlopen(userUrl)
    html = response.read().decode('utf-8')
    begin='[\s]*<p>[\s]*<small>[\s]*<b>[\s]*'
    middle='[\s]*</b>[\s]*</small>[\s]*</p>[\s]*<p>[\s]*'
    end='[\s]*</p>[\s]*</div>[\s]*<div class="tiny">[\s]*'
    szakmai = begin + 'szakmai' + middle + '([\w]*)' + end
    kozossegi = begin + 'k.z.ss.g' + middle + '([\w]*)' + end
    piaci = begin + 'piaci' + middle + '([\w]*)' + end
    blogok = begin + 'blogok, lok.l' + middle + '([\w]*)</p>[\s]*</div>'
    days= '.*azaz ([\d]+) napja'
    exp = szakmai + kozossegi + piaci + blogok + days
    regex = re.compile(exp, re.UNICODE | re.S)
    match = re.search(regex, html)
    szakmaiCount = int(match.group(1))
    kozossegiCount = int(match.group(2))
    piaciCount = int(match.group(3))
    blogokCount = int(match.group(4))
    days = int(match.group(5))
    total = szakmaiCount + kozossegiCount * 0.25 + piaciCount * 0.1
    rangok = [{'name':"újonc", 'posts':0, 'days':0},
              {'name':"lelkes újonc", 'posts':50, 'days':15},
              {'name':"kvázi-tag", 'posts':100, 'days':30},
              {'name':"tag", 'posts':200, 'days':60},
              {'name':"fanatikus tag", 'posts':400, 'days':100},
              {'name':"senior tag", 'posts':800, 'days':180},
              {'name':"őstag", 'posts':1750, 'days':270},
              {'name':"PH! addikt", 'posts':3500, 'days':365},
              {'name':"PH! kedvence", 'posts':6000, 'days':450},
              {'name':"PH! nagyúr", 'posts':10000, 'days':600},
              {'name':"PH! félisten", 'posts':17000, 'days':850},
              {'name':"Jómunkásember", 'posts':25000, 'days':1100}];
    
    #rangok = ["újonc", "lelkes újonc", "kvázi-tag", "tag", "fanatikus tag", "senior tag", "őstag", "PH! addikt", "PH! kedvence", "PH! nagyúr", "PH! félisten", "Jómunkásember"];
    napok = [0, 15, 30, 60, 100, 180, 270, 365, 450, 600, 850, 1100];
    actual = 0
    for i in range (0, len(rangok)):
        rangok[i]['missingDays'] = max(0,rangok[i]['days'] - days)
        rangok[i]['missingPosts'] = max(0,rangok[i]['posts'] - total)
        if rangok[i]['posts'] < total:
            actual = i

    return render(request, 'ph_rang/pages/index.html', {
        'szakmai': szakmaiCount,
        'kozossegi':kozossegiCount,
        'piaci':piaciCount,
        'blogok':blogokCount,
        'days':days,
        'total':total,
        'rangok':rangok,
        'i':actual,
        'userRang':rangok[actual]['name']
    })