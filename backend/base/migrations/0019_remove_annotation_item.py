# -*- coding: utf-8 -*-
# Generated by Django 1.11.5 on 2017-12-20 20:03
from __future__ import unicode_literals

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('base', '0018_auto_20171220_2300'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='annotation',
            name='item',
        ),
    ]