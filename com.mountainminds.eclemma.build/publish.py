#!/usr/bin/python

# You can specify credentials via environment variables AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY
# Or in boto config file ( see http://code.google.com/p/boto/wiki/BotoConfig ), for example in "~/.boto" as following:
# [Credentials]
# aws_access_key_id = {ACCESS KEY ID}
# aws_secret_access_key = {SECRET ACCESS KEY}

from boto.s3.connection import S3Connection
import sys
import os

BUCKET     = 'download.eclipselab.org'
BASE_PATH  = 'eclemma/trunk/update'

def delete_previous(bucket, dry_run):
    for key in bucket.get_all_keys(prefix=BASE_PATH):
        print 'Deleting %s' % key.name
        if not dry_run:
            key.delete()

def upload_current(bucket, dry_run, source_dir):
    # remove trailing slash if it exists
    if source_dir[-1:] == "/":
        source_dir = source_dir[0:-1]

    for root, dirs, files in os.walk(source_dir):
        dirpath = BASE_PATH + root[len(source_dir):]
        for f in files:
            print "Uploading %s/%s" % (dirpath, f)
            if not dry_run:
                key = bucket.new_key(dirpath + '/' + f)
                key.set_contents_from_filename(root + '/' + f)
                key.set_acl('public-read')

basedir = os.path.abspath(os.path.dirname(__file__))
source_dir = basedir + "/../com.mountainminds.eclemma.site/target/repository"

# prevent accidental execution
dry_run = True
if len(sys.argv) == 2 and sys.argv[1] == "-x":
    dry_run = False

if dry_run:
    print "DRY RUN: specify option '-x' for actual execution"

# sanity check
if not os.path.isfile(source_dir + "/content.jar"):
    sys.exit("content.jar not found in %s" % source_dir)

connection = S3Connection('AKIAJITWFJUZ4YRWC2HA', 'y2XMrMpdkl4bmy4eTSmP2OmEBa9TvOjW8AvN1mlw')
bucket = connection.get_bucket(BUCKET)

delete_previous(bucket, dry_run)
upload_current(bucket, dry_run, source_dir)
