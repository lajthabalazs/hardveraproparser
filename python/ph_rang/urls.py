from django.conf.urls import patterns, include, url
from ph_rang import pages

from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'ph_rang.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),
    url(r'.*', pages.index, name='index'),
)
