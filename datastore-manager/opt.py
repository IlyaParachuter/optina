import os
import sys
import threading
import httplib
import urllib2
import re
import argparse
from config import Config

import datetime
import time
import calendar


class PACV:
  def __init__(self, part, abbr, chap, verse):
    self.part = part
    self.abbr = abbr
    self.chap = chap
    self.verse = verse

  @property
  def part(self):
    return self.part

  @property
  def abbr(self):
    return self.abbr

  @property
  def chap(self):
    return self.chap

  @property
  def verse(self):
    return self.verse


class MainApp:
  '''
  __books = {
    'old' : ['gen', 'ish', 'lev', 'chis', 'vtor', 'nav', 'sud', 'ruf',
             '1ts', '2ts', '3ts', '4ts', '1par', '2par', 'ezd', 'neem',
             '2ezd', 'tov', 'iud', 'esf', 'pr', 'ekl', 'pp', 'prs',
             'prsir', 'iov', 'is', 'ier', 'pier', 'var', 'iez', 'dan',
             'os', 'iol', 'am', 'av', 'ion', 'mih', 'naum', 'avm',
             'sof', 'ag', 'zah', 'mal', '1mak', '2mak', '3mak', '3ezd',
             'ps'],
    'new' : ['mf', 'mk', 'lk', 'in', 'act', 'iak', '1pet', '2pet',
             '1in', '2in', '3in', 'iud', 'rim', '1kor', '2kor', 'gal',
             'ef', 'fil', 'kol', '1sol', '2sol', '1tim', '2tim', 'tit',
             'fm', 'evr', 'otkr']
  }

  '''
  __books = {
    'old' : ['gen', 'ish'],
    'new' : ['mf', 'mk']
  }

  def __MkTree(self):
    for part in self.__books:
      part_path = self.__data_dir + '\\' + part + '\\'
      if not os.path.isdir(part_path):
        os.mkdir(part_path)

      for book in self.__books[part]:
        book_path = part_path + book
        if not os.path.isdir(book_path):
          os.mkdir(book_path)

  def __MkChapDir(self, part, abbr, chap):
    chap_path = '{}//{}//{}//{}'.format(self.__data_dir, part, abbr, chap)
    if not os.path.isdir(chap_path):
      os.mkdir(chap_path)

  def __read_text(self, base_url, what, size = None):
    res = ''
    retries_count = 0
    i = self.__max_retries
    while True:
      try:
        f_in = self.__opener.open(base_url + what)
      except httplib.HTTPException:
        break
      
      try:
        res = f_in.read(size)
        f_in.close()
        break
      except httplib.IncompleteRead:
        f_in.close()
        i = i - 1
        retries_count = self.__max_retries - i
        if retries_count <= self.__max_retries:
          self.__lock.acquire()
          print '%s retry # %d' % (what, retries_count)
          self.__lock.release()
        else:
          raise httplib.IncompleteRead
          break

    return res

  def __pacv2fname(self, pacv):
    data_dir = self.__data_dir
    return (pacv is None) and '{}\\start'.format(data_dir) or '{}\\{}\\{}\\{}\\{}'.format(data_dir, pacv.part, pacv.abbr, pacv.chap, pacv.verse)

  def __get_text(self, what=None, pacv=None):
    if what is None:
      what = ''

    res = None
    fname = self.__pacv2fname(pacv)
    if os.path.isfile(fname):
        f_out = open(fname, 'r')
        res = f_out.read()
        f_out.close()
    else:
      s = self.__read_text(self.__base_url, what, self.__html_sig_length)
      if s != self.__html_sig:
        s = self.__read_text(self.__base_url, what)
        f_out = open(fname, 'w')
        f_out.write(s)
        f_out.close()
        res = s
    
    return res
  
  def __get_verses(self, lst):
    for v in lst:
      stime = time.strftime('%d.%m.%Y %H:%M:%S', v[0])
      evt_time = time.mktime(v[0])
      fname = self.__what2fname(v[1])
      bExists = os.path.exists(fname)
      if not bExists:
        self.__lock.acquire()
        print '%s: %s [download]' % (stime, v[1])
        self.__lock.release()
        self.__get_text(v[1])
      elif evt_time > os.path.getmtime(fname):
        self.__lock.acquire()
        print '%s: %s [update]' % (stime, v[1])
        self.__lock.release()
        os.remove(fname)
        self.__get_text(v[1])
      else:
        self.__lock.acquire()
        print '%s: %s [skip]' % (stime, v[1])
        self.__lock.release()

        
  def __get_lines(self, part, abbr, chap, prog, lines):
    if abbr == 'ps':
      fmt = '%s:%s:%03d:%02d'
    else:
      fmt = '%s:%s:%02d:%02d'

    i = 0

    lines_length = len(lines)
    while i < lines_length:
      s = lines[i][0]
      if len(s) > 0:
        m = prog.match(s)
        if m:
          verse = int(m.group(1))
          what = fmt % (part, abbr, chap, verse)
          pacv = PACV(part, abbr, chap, verse)
          res = os.path.isfile(self.__pacv2fname(pacv))
          if not res:
            res = self.__get_text(what, pacv) != None
            self.__lock.acquire()
            l = lines[i][0]
            lines[i] = (l, verse, res)
            self.__lock.release()

      i = i + 1

  def __do_parallel(self, lst, handler, threads_count, args = None):
    total_items = len(lst)
    if args == None:
      args = ()
    
    if total_items < threads_count:
      threads_count = total_items

    lst_grp = []
    beg = 0
    items_per_thread = total_items // threads_count
    items_extra = total_items % threads_count

    for i in range(threads_count):
      n = items_per_thread
      if items_extra:
        n += 1
        items_extra -= 1
      
      lst_grp.append(lst[beg:beg + n])
      beg += n

    threads = [threading.Thread(target=handler, args=args + (x, )) for x in lst_grp]

    for t in threads:
      t.start()

    for t in threads:
      t.join()

    res=[]
    for l in lst_grp:
      res.extend(l)
    
    return res

  def __get_chapter(self, part, abbr, chap, start, prog):
    res = ([], [])
    lines = map(lambda x: (x, None), start.split('\n'))
    lst = self.__do_parallel(lines, self.__get_lines, self.__verse_threads_count, (part, abbr, chap, prog))
    self.__lock.acquire()
    for l in lst:
      v = l[1]
      if v != None:
        res[l[2] and 1 or 0].append(v)

    self.__lock.release()
    return res
  
  def __get_ranges(self, a):
    res = ''
    a_len = len(a)
    if a_len:
      v_old = a[0]
      res = '%d' % v_old
      range_len = 0
      for v in a[1:]:
        if v - v_old > 1:
          if range_len > 1:
            res += '..%d' % v_old
          
          res += ', %d' % v
          range_len = 0
        else:
          range_len += 1
        
        v_old = v
      
      if range_len > 1:
        res += '..%d' % v_old
    
    return res

  def __get_book(self, part, abbr):
    chap = 1
    res = True
    while res:
      if abbr == 'ps':
        fmt = '%s:%s:%03d'
        fmtp = ':*%s[:;]%s[:;]%03d'
      else:
        fmt = '%s:%s:%02d'
        fmtp = ':*%s[:;]%s[:;]%02d'

      pacv = PACV(part, abbr, chap, 'start')
      self.__MkChapDir(part, abbr, chap)
      res = self.__get_text((fmt + ':start') % (part, abbr, chap), pacv)
      if res:
        pattern = ('.*\[\[%s[:;](\d+)\|' % fmtp) % (part, abbr, chap)
        prog = re.compile(pattern)
        
        absent, downloaded = self.__get_chapter(part, abbr, chap, res, prog)
        self.__lock.acquire()

        s = ''
        if len(downloaded):
          s += 'downloaded: %s' % self.__get_ranges(downloaded)

        if len(absent):
          if len(s):
            s += '; '

          s += 'absent: %s' % self.__get_ranges(absent)
          
        if len(s) == 0:
          print (fmt +  ' is ok.') % (part, abbr, chap)
        else:
          print (fmt + ' ' + s) % (part, abbr, chap)

        self.__lock.release()

        chap += 1

    self.__lock.acquire()
    print '%s:%s is ok.' % (part, abbr)
    self.__lock.release()

  def __get_books(self, a):
    for part, abbr in a:
      self.__get_book(part, abbr)

  def __init__(self):
    argparser = argparse.ArgumentParser(description='Collect opt. wiki.')
    argparser.add_argument('-d', '--download', action='store_true',
                           help='download files that missing locally')
    argparser.add_argument('-u', '--update', action='store_true',
                           help='update files from RSS feed')
    self.__args = argparser.parse_args()

    self.__cfg = cfg = Config('opt.cfg')

    self.__data_dir = self.__cfg.common.get('data_dir', 'data')
    if not os.path.exists(self.__data_dir):
      os.makedirs(self.__data_dir)

    if cfg.proxy.get('type', 'none') == 'http':
      proxy = urllib2.ProxyHandler({'http': '%s:%d' % (cfg.proxy.get('address', '127.0.0.1'), cfg.proxy.get('port', 3128))})
      self.__opener = urllib2.build_opener(proxy)
    else:
      self.__opener = urllib2.build_opener()
    
    user_agent = cfg.http.get('user_agent')
    if user_agent != None:
      self.__opener.addheaders = [('User-Agent', user_agent)]

    self.__base_url = cfg.http.get('base_url', 'http://bible.optina.ru/_export/raw/')
    self.__html_sig = cfg.http.get('html_sig', '<!DOCTYPE html')
    self.__html_sig_length = len(self.__html_sig)
    self.__max_retries = cfg.http.get('max_retries', 33)
    self.__lock = threading.Lock()

  def __first_modified_file(self):
    import glob
    
    first_name = None
    first_date = None
    for name in glob.iglob(self.__data_dir + "/*"):
      if os.path.isfile(name):
        if not first_name:
          first_name = name
          first_date = os.path.getmtime(name)
        else:
          date2 = os.path.getmtime(name)
          if date2 < first_date:
            first_name = name
            first_date = date2

    return first_name, first_date

  def __touch_files(self, tm):
    import glob
    
    for name in glob.iglob(self.__data_dir + "/*"):
      if os.path.isfile(name):
        if tm > os.path.getmtime(name):
          os.utime(name, (tm, tm))

  def run(self):
    cfg = self.__cfg
    if self.__args.download:
      self.__MkTree()
      if self.__get_text() != None:
        total_books = 0
        all_lin = []
        for k in self.__books.keys():
          total_books += len(self.__books[k])
          all_lin.extend(map(lambda x: (k, x), self.__books[k]))

        book_threads_count = cfg.common.get('book_threads_count', 2)
        self.__verse_threads_count = cfg.common.get('verse_threads_count', 8)
        print 'total_books = %d' % total_books
        self.__do_parallel(all_lin, self.__get_books, book_threads_count)

    elif self.__args.update:
      import feedparser
      
      num = cfg.rss.get('start_entries', 10)
      max_num = cfg.rss.get('max_entries')
      lst = None
      lst_len = 0
      sys.stdout.write('Find oldest file...')
      first_data = self.__first_modified_file()[1]
      print 'Ok!'
      print 'Parsing RSS feed:'
      while True:
        base_url = 'http://bible.optina.ru/'
        what = 'feed.php?num=%d' % num
        #print 'url = %s%s' % (base_url, what)
        res = self.__read_text(base_url, what)
        feed = feedparser.parse(res)

        last_data = first_data
        lst = []
        #print 'len = %s' % len(feed['entries'])
        #for i in range(num):
        real_num = len(feed['entries'])
        for i in range(real_num):
          evt_data = time.mktime(feed['entries'][i]['updated_parsed']) - time.timezone
          if evt_data > first_data:
            lst.append((time.localtime(evt_data), feed['entries'][i]['title']))
            
            if evt_data >= last_data:
              last_data = evt_data

          else:
            break
        
        lst_len = len(lst)

        #if lst_len < num:
        if lst_len < real_num:
          #print 'Hit %d from %d entries!' % (lst_len, num)
          print 'Hit %d from %d entries!' % (lst_len, real_num)
          break
        
        if real_num < num:
          print 'Hit %d from %d entries, no more entries available!' % (lst_len, real_num)
          break
          
        print 'Hit %d from %d entries, go further...' % (lst_len, num)
        num += num
        if max_num and (num > max_num):
          print 'Maximum allowed numbers of entries has been reached! Please edit configuration file or use download mode.'
          break

      if not max_num or (num <= max_num):
        prog = re.compile(r'(\w+:\w+:\d+:(?:\d+|start))')
        for i in range(len(lst)):
          evt = lst[i]
          #print evt[1], prog.match(evt[1])
          m = prog.match(evt[1])
          lst[i] = evt[0], m.group(1)
        
        update_threads_count = cfg.common.get('update_threads_count', 4)
        if lst_len:
          self.__do_parallel(lst, self.__get_verses, update_threads_count)
        
          sys.stdout.write('Touch files...')
          self.__touch_files(last_data)
          print 'Ok!'

MainApp().run()