'use strict';

/** 
 * Module listing all application Filters
 */
angular.module('leaveManagerApp.filters', []).

/** 
 * Filter for getting tooltip for completed agents. This should be moved to directives.
 */
filter('colorFilter', [ function() {
  return function(status) {
    var bgColorClass = '';
    if(status.toUpperCase() == 'PENDING')
    {
        bgColorClass = 'md-status-pending';
    }
    else if(status.toUpperCase() == 'APPROVED')
    {
        bgColorClass = 'md-status-approved';
    }
    else if(status.toUpperCase() == 'REJECTED')
    {
        bgColorClass = 'md-status-rejected';
    }
    else if(status.toUpperCase() == 'CANCELLED')
    {
        bgColorClass = 'md-status-cancelled';
    }
    return bgColorClass;
  };
} ])
    .filter('statusIcon', [ function() {
        return function(status) {
            var statusIcon = '';
            if(status.toUpperCase() == 'PENDING')
            {
                statusIcon = 'glyphicon-warning-sign';
            }
            else if(status.toUpperCase() == 'APPROVED')
            {
                statusIcon = 'glyphicon-ok';
            }
            else if(status.toUpperCase() == 'REJECTED')
            {
                statusIcon = 'glyphicon-thumbs-down';
            }
            else if(status.toUpperCase() == 'CANCELLED')
            {
                statusIcon = 'glyphicon-remove';
            }
            return statusIcon;
        };
    } ])
    .filter('iconFillColor', [ function() {
        return function(status) {
            var fillColor = '';
            if(status.toUpperCase() == 'PENDING')
            {
                fillColor = '#db7560';
            }
            else if(status.toUpperCase() == 'APPROVED')
            {
                fillColor = '#5093b7';
            }
            else if(status.toUpperCase() == 'REJECTED')
            {
                fillColor = '#000000';
            }
            else if(status.toUpperCase() == 'CANCELLED')
            {
                fillColor = '#000000';
            }
            return 'fill:'+fillColor;
        };
    } ]);

