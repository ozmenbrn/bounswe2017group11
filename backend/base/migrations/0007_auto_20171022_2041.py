# -*- coding: utf-8 -*-
# Generated by Django 1.11.5 on 2017-10-22 17:41
from __future__ import unicode_literals

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('base', '0006_auto_20171022_2030'),
    ]

    operations = [
        migrations.RenameField(
            model_name='timeline',
            old_name='item_id',
            new_name='item',
        ),
        migrations.RenameField(
            model_name='timeline',
            old_name='location_id',
            new_name='location',
        ),
    ]
