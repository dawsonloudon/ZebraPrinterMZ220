var exec = require("cordova/exec");

var zPrinter = {

    findPrinters: function(timer,callback) {
        cordova.exec(
            function(result) {
                clearInterval(timer);
                if(callback && typeof(callback) === 'function') {
                    callback();
                }
            },
            function() {
                alert('Could not find printer');
            },
            'ZebraPrinterMZ220',
            'findPrinters',
            []
        );
    },
    getList: function(callback) {
        cordova.exec(
            function(result) {
                if(callback && typeof(callback) === 'function') {
                    callback(result);
                }
            },
            function() {
                alert('Could not get printer');
            },
            'ZebraPrinterMZ220',
            'getDevices',
            []
        );
    },
    openConnection: function(printer) {
        cordova.exec(
            null,
            null,
            'ZebraPrinterMZ220',
            'openConnection',
            [printer]
        );
    },
    closeConnection: function(callback) {
        cordova.exec(
            function(){
                if(callback && typeof(callback) === 'function') {
                    callback();
                }
            },
            null,
            'ZebraPrinterMZ220',
            'closeConnection',
            []
        );
    },
    print: function(cpclData,callback) {
        cordova.exec(
            function() {
                if(callback && typeof(callback) === 'function') {
                    callback();
                }
            },
            null,
            'ZebraPrinterMZ220',
            'print',
            [cpclData]
        );
    }

};

module.exports = zPrinter;