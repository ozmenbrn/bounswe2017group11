# -*- coding: utf-8 -*-
# Generated by Django 1.11.5 on 2017-12-04 17:58
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('base', '0016_auto_20171204_2057'),
    ]

    operations = [
        migrations.AlterField(
            model_name='timeline',
            name='endDate',
            field=models.CharField(blank=True, max_length=20, null=True),
        ),
        migrations.AlterField(
            model_name='timeline',
            name='startDate',
            field=models.CharField(blank=True, max_length=20, null=True),
        ),
    ]